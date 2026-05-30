package com.sda.pharmacy.controller;

import com.sda.pharmacy.dto.LoginDTO;
import com.sda.pharmacy.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // -----------------------------------
    // Authenticate Admin / Staff Portal
    // -----------------------------------
    @PostMapping("/login")
    public String loginUser(
            @ModelAttribute LoginDTO loginDTO,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        boolean isValid = authService.loginUser(loginDTO);

        if (isValid) {
            // Destroy any existing session to prevent session fixation attacks
            request.getSession().invalidate();

            // Establish a fresh, secure HTTP session context
            HttpSession newSession = request.getSession(true);

            // Seed our core authentication tracking token parameter
            newSession.setAttribute(
                    "loggedInUser",
                    loginDTO.getFullName()
            );

            return "redirect:/dashboard";
        }

        // Return error message flash attributes back to our thymeleaf login frame
        redirectAttributes.addFlashAttribute(
                "error",
                "Username or password is incorrect"
        );

        return "redirect:/login";
    }

    // -----------------------------------
    // Explicit Session Termination (Logout)
    // -----------------------------------
    @GetMapping("/logout")
    public String logoutUser(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // Find current active user session without creating a placeholder new one
        HttpSession session = request.getSession(false);

        if (session != null) {
            // Purge and invalidate the active server session footprint completely
            // Purge and invalidate the active server session footprint completely
            session.invalidate();
        }

        // Send a success notification banner to display on the login page matrix
        redirectAttributes.addFlashAttribute(
                "success",
                "You have been securely logged out."
        );

        return "redirect:/login";
    }
}