package com.meditrack.pharmacy.builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceBuilder {

    private Invoice invoice;

    // Constructor

    public InvoiceBuilder() {

        invoice = new Invoice();

    }

    // Builder Methods

    public InvoiceBuilder setCustomerName(
            String customerName) {

        invoice.setCustomerName(customerName);

        return this;
    }

    public InvoiceBuilder setCustomerPhone(
            String customerPhone) {

        invoice.setCustomerPhone(customerPhone);

        return this;
    }

    public InvoiceBuilder setMedicineNames(
            List<String> medicineNames) {

        invoice.setMedicineNames(medicineNames);

        return this;
    }

    public InvoiceBuilder setQuantities(
            List<Integer> quantities) {

        invoice.setQuantities(quantities);

        return this;
    }

    public InvoiceBuilder setPrices(
            List<BigDecimal> prices) {

        invoice.setPrices(prices);

        return this;
    }

    public InvoiceBuilder setTotalAmount(
            BigDecimal totalAmount) {

        invoice.setTotalAmount(totalAmount);

        return this;
    }

    public InvoiceBuilder setInvoiceDate(
            LocalDateTime invoiceDate) {

        invoice.setInvoiceDate(invoiceDate);

        return this;
    }

    // Final Build Method

    public Invoice build() {

        return invoice;

    }
}