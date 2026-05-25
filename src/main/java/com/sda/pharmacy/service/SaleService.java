package com.sda.pharmacy.service;

import com.sda.pharmacy.builder.Invoice;
import com.sda.pharmacy.builder.InvoiceBuilder;
import com.sda.pharmacy.entity.*;

import com.sda.pharmacy.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SaleReportRepository salesReportRepository;

    // Create Sale + Generate Invoice

    public Invoice createSale(

            String customerName,
            String phoneNumber,

            int medicineId,
            int quantity

    ) {

        // Save Customer

        Customer customer = new Customer(
                customerName,
                phoneNumber
        );

        customerRepository.save(customer);

        // Fetch Medicine

        Medicine medicine =
                medicineRepository.findById(medicineId)
                        .orElseThrow();

        // Calculate Total

        BigDecimal subtotal =
                medicine.getPrice()
                        .multiply(BigDecimal.valueOf(quantity));

        // Create Sale

        Sale sale = new Sale();

        sale.setCustomer(customer);

        sale.setTotalAmount(subtotal);

        sale.setSaleDate(LocalDateTime.now());

        saleRepository.save(sale);

        // Create Sale Item

        SaleItem saleItem = new SaleItem();

        saleItem.setSale(sale);

        saleItem.setMedicine(medicine);

        saleItem.setQuantity(quantity);

        saleItem.setUnitPrice(medicine.getPrice());

        saleItem.setSubtotal(subtotal);

        // SAVE SALE ITEM
        // Trigger automatically reduces stock

        saleItemRepository.save(saleItem);

        // Build Invoice

        List<String> medicineNames =
                new ArrayList<>();

        medicineNames.add(
                medicine.getMedicineName()
        );

        List<Integer> quantities =
                new ArrayList<>();

        quantities.add(quantity);

        List<BigDecimal> prices =
                new ArrayList<>();

        prices.add(medicine.getPrice());

        Invoice invoice =
                new InvoiceBuilder()

                        .setCustomerName(customerName)

                        .setCustomerPhone(phoneNumber)

                        .setMedicineNames(medicineNames)

                        .setQuantities(quantities)

                        .setPrices(prices)

                        .setTotalAmount(subtotal)

                        .setInvoiceDate(LocalDateTime.now())

                        .build();

        return invoice;
    }

    public List<SalesReport> getSalesReports() {

        return salesReportRepository.findAll();

    }

    public BigDecimal getTotalRevenue() {

        return salesReportRepository.getTotalRevenue();

    }
}