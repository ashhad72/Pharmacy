package com.meditrack.pharmacy.dto;

public class CartItemDTO {
    private String medicineName;
    private int quantity;

    // Constructors
    public CartItemDTO() {}
    public CartItemDTO(String medicineName, int quantity) {
        this.medicineName = medicineName;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}