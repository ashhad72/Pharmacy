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

    // Main dashboard
    @GetMapping("/dashboard")
    public String dashboard() {

        return "dashboard";
    }

}
