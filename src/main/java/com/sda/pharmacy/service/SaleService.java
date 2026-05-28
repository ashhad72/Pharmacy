package com.sda.pharmacy.service;

import com.sda.pharmacy.builder.Invoice;
import com.sda.pharmacy.builder.InvoiceBuilder;
import com.sda.pharmacy.singleton.SystemLogger;

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
            int medicineId,
            int quantity
    ) {
        SystemLogger.getInstance()
                .log("SALES","Starting new sale process.");

        // 1. FETCH MEDICINE FIRST
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + medicineId));

        SystemLogger.getInstance()
                .log("SALES","Medicine fetched: " + medicine.getMedicineName());

        // 2. IMMEDIATE MANUAL STOCK DEDUCTION (Bypasses session overwrites)
        int currentStock = medicine.getQuantityInStock();
        if (currentStock < quantity) {
            throw new RuntimeException("Transaction Halted: Insufficient stock available! Left: " + currentStock);
        }

        // Decrement and explicitly flush it to the database table immediately
        medicine.setQuantityInStock(currentStock - quantity);
        medicineRepository.saveAndFlush(medicine);

        // -----------------------------------
        // Save Customer
        // -----------------------------------
        Customer customer = new Customer(customerName, phoneNumber);
        customerRepository.save(customer);

        // -----------------------------------
        // Calculate Total
        // -----------------------------------
        BigDecimal subtotal = medicine.getPrice().multiply(BigDecimal.valueOf(quantity));

        // -----------------------------------
        // Create Sale
        // -----------------------------------
        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setTotalAmount(subtotal);
        sale.setSaleDate(LocalDateTime.now());
        saleRepository.save(sale);

        // -----------------------------------
        // Create Sale Item (Points to already modified medicine state)
        // -----------------------------------
        SaleItem saleItem = new SaleItem();
        saleItem.setSale(sale);
        saleItem.setMedicine(medicine);
        saleItem.setQuantity(quantity);
        saleItem.setUnitPrice(medicine.getPrice());
        saleItem.setSubtotal(subtotal);

        // Save transactional lines
        saleItemRepository.save(saleItem);
        SystemLogger.getInstance().log("SALES","Sale saved successfully.");

        // -----------------------------------
        // Notify Observers
        // -----------------------------------
        inventoryManager.notifyObservers();
        SystemLogger.getInstance().log("SALES","Inventory observers notified.");

        // -----------------------------------
        // Build Invoice (Builder Pattern)
        // -----------------------------------
// -----------------------------------
        // Build Invoice (Builder Pattern)
        // -----------------------------------
        List<String> medicineNames = new ArrayList<>();
        medicineNames.add(medicine.getMedicineName());

        List<Integer> quantities = new ArrayList<>();
        quantities.add(quantity);

        List<BigDecimal> prices = new ArrayList<>();
        prices.add(medicine.getPrice());

        // --- START OF FORMATTING ADJUSTMENT ---
        // 1. Create separate style patterns for Date and Time
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");


        Invoice invoice = new InvoiceBuilder()
                .setCustomerName(customerName)
                .setCustomerPhone(phoneNumber)
                .setMedicineNames(medicineNames)
                .setQuantities(quantities)
                .setPrices(prices)
                .setTotalAmount(subtotal)
                .setInvoiceDate(LocalDateTime.now()) // <-- Passes the beautifully formatted split text to the UI
                .build();

        SystemLogger.getInstance().log("SALES","Invoice generated successfully.");
        SystemLogger.getInstance().log("SALES","Sale process completed successfully.");

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
}