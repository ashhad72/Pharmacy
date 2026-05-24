package com.sda.pharmacy.controller;

import com.sda.pharmacy.dto.LoginDTO;
import com.sda.pharmacy.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // Login Admin/Pharmacy Worker
    @PostMapping("/login")
    public String loginUser(
            @ModelAttribute LoginDTO loginDTO) {

        boolean isValid =
                authService.loginUser(loginDTO);

        // Successful Login
        if (isValid) {

            return "redirect:/dashboard";
        }

        // Invalid Login
        return "redirect:/login";
    }
}