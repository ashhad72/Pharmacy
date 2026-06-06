package com.sda.pharmacy.service;

import com.sda.pharmacy.builder.Invoice;
import com.sda.pharmacy.builder.InvoiceBuilder;
import com.sda.pharmacy.singleton.SystemLogger;

import com.sda.pharmacy.exception.InsufficientStockException;
import com.sda.pharmacy.entity.Customer;
import com.sda.pharmacy.entity.Medicine;
import com.sda.pharmacy.entity.Sale;
import com.sda.pharmacy.entity.SaleItem;
import com.sda.pharmacy.entity.SalesReport;

import com.sda.pharmacy.observer.InventoryManager;
import org.springframework.transaction.annotation.Transactional;
import com.sda.pharmacy.repository.CustomerRepository;
import com.sda.pharmacy.repository.MedicineRepository;
import com.sda.pharmacy.repository.SaleItemRepository;
import com.sda.pharmacy.repository.SaleReportRepository;
import com.sda.pharmacy.repository.SaleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

        this.saleRepository = saleRepository;

        this.saleItemRepository = saleItemRepository;

        this.medicineRepository = medicineRepository;

        this.customerRepository = customerRepository;

        this.salesReportRepository = salesReportRepository;

        this.inventoryManager = inventoryManager;
    }


    // -----------------------------------
    // Create Sale + Generate Invoice
    // Builder Pattern
    // Trigger automatically reduces stock
    // Observer notified after stock change
    // -----------------------------------
    @org.springframework.transaction.annotation.Transactional // Ensures all database operations commit safely together
    public Invoice createSale(
            String customerName,
            String phoneNumber,
            List<Integer> medicineIds, // ◄ Changed from int to List<Integer>
            List<Integer> quantities   // ◄ Changed from int to List<Integer>
    ) {
        SystemLogger.getInstance()
                .log("SALES", "Starting new multi-item sale process.");

        // Defensive check to avoid index bugs
        if (medicineIds == null || quantities == null || medicineIds.size() != quantities.size() || medicineIds.isEmpty()) {
            throw new IllegalArgumentException("Transaction Halted: Medicine entries and quantities mismatch.");
        }

        // -----------------------------------
        // Save Customer
        // -----------------------------------
        Customer customer = new Customer(customerName, phoneNumber);
        customerRepository.save(customer);

        // -----------------------------------
        // Initialize Transaction Trackers
        // -----------------------------------
        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setSaleDate(LocalDateTime.now());

        BigDecimal totalInvoiceAmount = BigDecimal.ZERO;

        // Lists required to feed your custom InvoiceBuilder later
        List<String> builderMedicineNames = new ArrayList<>();
        List<Integer> builderQuantities = new ArrayList<>();
        List<BigDecimal> builderPrices = new ArrayList<>();

        // -----------------------------------
        // Loop Through All Items in the Request
        // -----------------------------------
        for (int i = 0; i < medicineIds.size(); i++) {
            int medicineId = medicineIds.get(i);
            int quantity = quantities.get(i);

            // 1. FETCH MEDICINE FIRST
            Medicine medicine = medicineRepository.findById(medicineId)
                    .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + medicineId));

            SystemLogger.getInstance()
                    .log("SALES", "Medicine fetched: " + medicine.getMedicineName());

            // 2. IMMEDIATE MANUAL STOCK DEDUCTION (Bypasses session overwrites)
            int currentStock = medicine.getQuantityInStock();
            if (currentStock < quantity) {
                throw new InsufficientStockException(
                        "Insufficient stock available for "
                                + medicine.getMedicineName()
                                + "! Left: " + currentStock
                );
            }

            // Decrement and explicitly flush it to the database table immediately
            medicine.setQuantityInStock(currentStock - quantity);
            medicineRepository.saveAndFlush(medicine);

            // 3. Calculate Item Total
            BigDecimal itemSubtotal = medicine.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalInvoiceAmount = totalInvoiceAmount.add(itemSubtotal);

            // 4. Create Sale Item Record (Points to already modified medicine state)
            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setMedicine(medicine);
            saleItem.setQuantity(quantity);
            saleItem.setUnitPrice(medicine.getPrice());
            saleItem.setSubtotal(itemSubtotal);

            // Link child to master sale and persist individual transactional lines
            sale.addSaleItem(saleItem);

            // 5. Populate metadata buffers for the Invoice Builder lists
            builderMedicineNames.add(medicine.getMedicineName());
            builderQuantities.add(quantity);
            builderPrices.add(medicine.getPrice());
        }

        // Assign final calculated combined running total back to the master sale
        sale.setTotalAmount(totalInvoiceAmount);
        saleRepository.save(sale);

        SystemLogger.getInstance().log("SALES", "Sale saved successfully with ID: " + sale.getSaleId());

        // -----------------------------------
        // Notify Observers
        // -----------------------------------
        inventoryManager.notifyObservers();
        SystemLogger.getInstance().log("SALES", "Inventory observers notified.");

        // -----------------------------------
        // Build Invoice (Builder Pattern)
        // -----------------------------------
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");

        Invoice invoice = new InvoiceBuilder()
                .setCustomerName(customerName)
                .setCustomerPhone(phoneNumber)
                .setMedicineNames(builderMedicineNames) // Passes complete built arrays
                .setQuantities(builderQuantities)
                .setPrices(builderPrices)
                .setTotalAmount(totalInvoiceAmount)
                .setInvoiceDate(LocalDateTime.now())
                .build();

        SystemLogger.getInstance().log("SALES", "Invoice generated successfully.");
        SystemLogger.getInstance().log("SALES", "Sale process completed successfully.");

        return invoice;
    }
    // -----------------------------------
    // Sales Reports
    // -----------------------------------

    public List<SalesReport> getSalesReports() {

        return salesReportRepository.findAll();
    }

    // -----------------------------------
    // Total Revenue Function
    // -----------------------------------

    public BigDecimal getTotalRevenue() {

        return salesReportRepository
                .getTotalRevenue();
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
// Recent Sales (Transactions Panel)
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