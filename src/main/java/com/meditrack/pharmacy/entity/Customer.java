package com.meditrack.pharmacy.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "phone_number")
    private String phoneNumber;

    // Constructors

    public Customer() {
    }

    public Customer(String customerName, String phoneNumber) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}