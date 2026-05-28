package com.sda.pharmacy.controller;

import com.sda.pharmacy.dto.LoginDTO;
import com.sda.pharmacy.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // Login Admin/Pharmacy Worker
    @PostMapping("/login")
    public String loginUser(
            @ModelAttribute LoginDTO loginDTO,
            RedirectAttributes redirectAttributes) {

        boolean isValid =
                authService.loginUser(loginDTO);

        // Successful Login
        if (isValid) {

            return "redirect:/dashboard";
        }

        // Invalid Login
        redirectAttributes.addFlashAttribute(
                "error",
                "Username or password is incorrect"
        );

        return "redirect:/login";
    }
}