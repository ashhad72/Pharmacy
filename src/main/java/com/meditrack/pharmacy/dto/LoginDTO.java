package com.meditrack.pharmacy.dto;

public class LoginDTO {

    private String fullName;
    private String password;

    // Default Constructor
    public LoginDTO() {
    }

    // Parameterized Constructor
    public LoginDTO(String fullName, String password) {
        this.fullName = fullName;
        this.password = password;
    }

    // Getters and Setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
