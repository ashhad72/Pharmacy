package com.sda.pharmacy.controller;

import com.sda.pharmacy.dto.LoginDTO;
import com.sda.pharmacy.dto.RegisterDTO;
import com.sda.pharmacy.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // Register User
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute RegisterDTO registerDTO) {

        authService.registerUser(registerDTO);

        return "redirect:/login";
    }

    // Login User
    @PostMapping("/login")
    public String loginUser(
            @ModelAttribute LoginDTO loginDTO) {

        String result = authService.loginUser(loginDTO);

        // Admin Login
        if (result.contains("ADMIN")) {
            return "redirect:/admin/dashboard";
        }

        // User Login
        else if (result.contains("USER")) {
            return "redirect:/user/dashboard";
        }

        // Invalid Login
        return "redirect:/login";
    }
}
