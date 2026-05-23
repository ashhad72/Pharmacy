package com.sda.pharmacy.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Landing Page
    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    // Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Register Page
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Admin Dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    // User Dashboard
    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user-dashboard";
    }
}
