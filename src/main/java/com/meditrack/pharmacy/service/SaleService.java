package com.meditrack.pharmacy.service;

import com.meditrack.pharmacy.builder.Invoice;
import com.meditrack.pharmacy.builder.InvoiceBuilder;
import com.meditrack.pharmacy.singleton.SystemLogger;

import com.meditrack.pharmacy.exception.InsufficientStockException;
import com.meditrack.pharmacy.entity.Customer;
import com.meditrack.pharmacy.entity.Medicine;
import com.meditrack.pharmacy.entity.Sale;
import com.meditrack.pharmacy.entity.SaleItem;
import com.meditrack.pharmacy.entity.SalesReport;

import com.meditrack.pharmacy.observer.InventoryManager;
import com.meditrack.pharmacy.repository.CustomerRepository;
import com.meditrack.pharmacy.repository.MedicineRepository;
import com.meditrack.pharmacy.repository.SaleItemRepository;
import com.meditrack.pharmacy.repository.SaleReportRepository;
import com.meditrack.pharmacy.repository.SaleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final MedicineRepository medicineRepository;
    private final CustomerRepository customerRepository;
    private final SaleReportRepository salesReportRepository;
    private final InventoryManager inventoryManager;

    // -----------------------------------
    // Constructor Injection
    // -----------------------------------

    @Autowired
    public SaleService(
            SaleRepository saleRepository,
            SaleItemRepository saleItemRepository,
            MedicineRepository medicineRepository,
            CustomerRepository customerRepository,
            SaleReportRepository salesReportRepository,
            InventoryManager inventoryManager
    ) {
        this.saleRepository       = saleRepository;
        this.saleItemRepository   = saleItemRepository;
        this.medicineRepository   = medicineRepository;
        this.customerRepository   = customerRepository;
        this.salesReportRepository = salesReportRepository;
        this.inventoryManager     = inventoryManager;
    }

    // -----------------------------------
    // Sale Result Wrapper
    // Carries invoice + optional low stock warnings
    // Does not block the sale — purely informational
    // -----------------------------------

    public record SaleResult(Invoice invoice, List<String> warnings) {
        public boolean hasWarnings() {
            return warnings != null && !warnings.isEmpty();
        }
    }

    // -----------------------------------
    // Create Sale + Generate Invoice
    // Builder Pattern
    // Trigger automatically reduces stock
    // Observer notified after stock change
    // -----------------------------------

    @org.springframework.transaction.annotation.Transactional
    public SaleResult createSale(
            String customerName,
            String phoneNumber,
            List<Integer> medicineIds,
            List<Integer> quantities
    ) {
        SystemLogger.getInstance()
                .log("SALES", "Starting new multi-item sale process.");

        // -----------------------------------
        // Defensive input validation
        // -----------------------------------

        if (medicineIds == null
                || quantities == null
                || medicineIds.size() != quantities.size()
                || medicineIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Transaction Halted: Medicine entries and quantities mismatch."
            );
        }

        // -----------------------------------
        // Save Customer
        // -----------------------------------

        Customer customer = new Customer(customerName, phoneNumber);
        customerRepository.save(customer);

        // -----------------------------------
        // Initialize Sale + Trackers
        // -----------------------------------

        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setSaleDate(LocalDateTime.now());

        BigDecimal totalInvoiceAmount = BigDecimal.ZERO;

        List<String>     builderMedicineNames = new ArrayList<>();
        List<Integer>    builderQuantities    = new ArrayList<>();
        List<BigDecimal> builderPrices        = new ArrayList<>();

        // Collects low stock warnings — does NOT stop the sale
        List<String> lowStockWarnings = new ArrayList<>();

        // -----------------------------------
        // Loop Through All Cart Items
        // -----------------------------------

        for (int i = 0; i < medicineIds.size(); i++) {

            int medicineId = medicineIds.get(i);
            int quantity   = quantities.get(i);

            // 1. Fetch medicine
            Medicine medicine = medicineRepository.findById(medicineId)
                    .orElseThrow(() -> new RuntimeException(
                            "Medicine not found with ID: " + medicineId
                    ));

            SystemLogger.getInstance()
                    .log("SALES", "Medicine fetched: " + medicine.getMedicineName());

            // 2. EXPIRY CHECK — hard block, do not dispense expired medicine
            if (medicine.getExpiryDate() != null
                    && LocalDate.now().isAfter(medicine.getExpiryDate())) {

                SystemLogger.getInstance()
                        .log("SALES", "Blocked: Expired medicine — " + medicine.getMedicineName());

                throw new RuntimeException(
                        "Cannot dispense '" + medicine.getMedicineName()
                                + "' — this medicine expired on " + medicine.getExpiryDate() + "."
                );
            }

            // 3. STOCK CHECK — hard block if not enough units
            int currentStock = medicine.getQuantityInStock();

            if (currentStock < quantity) {

                SystemLogger.getInstance()
                        .log("SALES", "Blocked: Insufficient stock — " + medicine.getMedicineName());

                throw new InsufficientStockException(
                        "Insufficient stock for '"
                                + medicine.getMedicineName()
                                + "'. Requested: " + quantity
                                + ", Available: " + currentStock + "."
                );
            }

            // 4. LOW STOCK WARNING — soft alert, sale continues
            int stockAfterSale = currentStock - quantity;

            if (stockAfterSale <= 20) {

                SystemLogger.getInstance()
                        .log("SALES", "Low stock warning: " + medicine.getMedicineName()
                                + " → " + stockAfterSale + " units remaining.");

                lowStockWarnings.add(
                        medicine.getMedicineName()
                                + " — only " + stockAfterSale + " unit"
                                + (stockAfterSale == 1 ? "" : "s") + " remaining after this sale."
                );
            }

            // 5. Deduct stock and flush immediately
            medicine.setQuantityInStock(stockAfterSale);
            medicineRepository.saveAndFlush(medicine);

            // 6. Calculate item subtotal
            BigDecimal itemSubtotal = medicine.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            totalInvoiceAmount = totalInvoiceAmount.add(itemSubtotal);

            // 7. Build sale item record
            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setMedicine(medicine);
            saleItem.setQuantity(quantity);
            saleItem.setUnitPrice(medicine.getPrice());
            saleItem.setSubtotal(itemSubtotal);
            sale.addSaleItem(saleItem);

            // 8. Feed invoice builder buffers
            builderMedicineNames.add(medicine.getMedicineName());
            builderQuantities.add(quantity);
            builderPrices.add(medicine.getPrice());
        }

        // -----------------------------------
        // Persist Final Sale
        // -----------------------------------

        sale.setTotalAmount(totalInvoiceAmount);
        saleRepository.save(sale);

        SystemLogger.getInstance()
                .log("SALES", "Sale saved successfully. ID: " + sale.getSaleId());

        // -----------------------------------
        // Notify Observers
        // -----------------------------------

        inventoryManager.notifyObservers();

        SystemLogger.getInstance()
                .log("SALES", "Inventory observers notified.");

        // -----------------------------------
        // Build Invoice (Builder Pattern)
        // -----------------------------------

        Invoice invoice = new InvoiceBuilder()
                .setCustomerName(customerName)
                .setCustomerPhone(phoneNumber)
                .setMedicineNames(builderMedicineNames)
                .setQuantities(builderQuantities)
                .setPrices(builderPrices)
                .setTotalAmount(totalInvoiceAmount)
                .setInvoiceDate(LocalDateTime.now())
                .build();

        SystemLogger.getInstance()
                .log("SALES", "Invoice generated successfully.");

        SystemLogger.getInstance()
                .log("SALES", "Sale process completed successfully.");

        // -----------------------------------
        // Return Invoice + Warnings
        // -----------------------------------

        return new SaleResult(invoice, lowStockWarnings);
    }

    // -----------------------------------
    // Sales Reports
    // -----------------------------------

    public List<SalesReport> getSalesReports() {
        return salesReportRepository.findAll();
    }

    // -----------------------------------
    // Total Revenue
    // -----------------------------------

    public BigDecimal getTotalRevenue() {
        return salesReportRepository.getTotalRevenue();
    }

    // -----------------------------------
    // Today's Revenue
    // -----------------------------------

    public BigDecimal getTodayRevenue() {
        SystemLogger.getInstance()
                .log("DASHBOARD", "Fetching today's revenue.");
        return saleRepository.getTodayRevenue();
    }

    // -----------------------------------
    // Today's Transaction Count
    // -----------------------------------

    public int getTodayTransactionCount() {
        SystemLogger.getInstance()
                .log("DASHBOARD", "Fetching today's transaction count.");
        return saleRepository.getTodayTransactionCount();
    }

    // -----------------------------------
    // Monthly Revenue
    // -----------------------------------

    public BigDecimal getMonthlyRevenue() {
        SystemLogger.getInstance()
                .log("DASHBOARD", "Fetching monthly revenue.");
        return saleRepository.getMonthlyRevenue();
    }

    // -----------------------------------
    // Last Month Revenue
    // -----------------------------------

    public BigDecimal getLastMonthRevenue() {
        SystemLogger.getInstance()
                .log("DASHBOARD", "Fetching last month revenue.");
        return saleRepository.getLastMonthRevenue();
    }

    // -----------------------------------
    // Recent Sales
    // -----------------------------------

    public List<Sale> getRecentSales() {
        SystemLogger.getInstance()
                .log("DASHBOARD", "Fetching recent sales.");
        return saleRepository.getRecentSales();
    }

    // -----------------------------------
    // Monthly Revenue Chart Data
    // -----------------------------------

    public List<Object[]> getMonthlyRevenueChart() {
        SystemLogger.getInstance()
                .log("DASHBOARD", "Fetching monthly revenue chart data.");
        return saleRepository.getMonthlyRevenueChart();
    }
}