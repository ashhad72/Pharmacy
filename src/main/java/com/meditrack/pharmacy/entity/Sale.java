package com.meditrack.pharmacy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private int saleId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    // ── NEW FIELD ADDED HERE ──
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    // Default Constructor
    public Sale() {
    }

    // Parameterized Constructor (UPDATED to initialize the items list)
    public Sale(Customer customer, BigDecimal totalAmount) {
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.saleDate = LocalDateTime.now();
        this.items = new ArrayList<>(); // Initializes the line items collection
    }

    // Getters and Setters

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    // ── NEW GETTERS, SETTERS, AND HELPER METHODS ADDED HERE ──

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }

    /**
     * Helper method to link a sale item line row to this master sale record
     * while preserving bidirectional sync for JPA/Hibernate operations.
     */
    public void addSaleItem(SaleItem item) {
        items.add(item);
        item.setSale(this);
    }
}