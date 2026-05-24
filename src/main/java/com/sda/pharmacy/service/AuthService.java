package com.sda.pharmacy.service;

import com.sda.pharmacy.dto.LoginDTO;
import com.sda.pharmacy.entity.User;
import com.sda.pharmacy.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // Login Authentication
    public boolean loginUser(LoginDTO loginDTO) {

        User user =
                userRepository.findByEmail(
                        loginDTO.getEmail()
                );

        // Check User Exists
        if (user == null) {

            return false;
        }

        // Check Password
        return user.getPassword()
                .equals(loginDTO.getPassword());
    }
}