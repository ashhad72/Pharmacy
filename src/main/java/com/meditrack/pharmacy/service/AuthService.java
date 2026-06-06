package com.meditrack.pharmacy.service;

import com.meditrack.pharmacy.dto.LoginDTO;
import com.meditrack.pharmacy.entity.User;
import com.meditrack.pharmacy.repository.UserRepository;
import com.meditrack.pharmacy.singleton.SystemLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // Login Authentication
    public boolean loginUser(LoginDTO loginDTO) {
        SystemLogger.getInstance()
                .log("AUTH","Login attempt for Username: "
                        + loginDTO.getFullName());
        User user =
                userRepository.findByFullName(
                        loginDTO.getFullName()
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
                            + loginDTO.getFullName());

        } else {

            SystemLogger.getInstance()
                    .log("AUTH","Login failed: incorrect password.");
        }

        return passwordMatches;
    }
}