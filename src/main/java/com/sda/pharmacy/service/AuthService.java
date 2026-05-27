package com.sda.pharmacy.service;

import com.sda.pharmacy.dto.LoginDTO;
import com.sda.pharmacy.entity.User;
import com.sda.pharmacy.repository.UserRepository;
import com.sda.pharmacy.singleton.SystemLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // Login Authentication
    public boolean loginUser(LoginDTO loginDTO) {
        SystemLogger.getInstance()
                .log("AUTH","Login attempt for email: "
                        + loginDTO.getEmail());
        User user =
                userRepository.findByEmail(
                        loginDTO.getEmail()
                );

        // Check User Exists
        if (user == null) {

            SystemLogger.getInstance()
                    .log("AUTH", "Login Failed: User Not Found");
            return false;
        }

        // Check Password
        boolean passwordMatches =

                user.getPassword()
                        .equals(loginDTO.getPassword());

        if (passwordMatches) {

            SystemLogger.getInstance()
                    .log("AUTH","Login successful for: "
                            + loginDTO.getEmail());

        } else {

            SystemLogger.getInstance()
                    .log("AUTH","Login failed: incorrect password.");
        }

        return passwordMatches;
    }
}