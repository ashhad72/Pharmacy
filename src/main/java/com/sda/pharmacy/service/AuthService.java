package com.sda.pharmacy.service;


import com.sda.pharmacy.dto.LoginDTO;
import com.sda.pharmacy.dto.RegisterDTO;
import com.sda.pharmacy.entity.User;
import com.sda.pharmacy.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // Register User
    public String registerUser(RegisterDTO registerDTO) {

        Optional<User> existingUser =
                userRepository.findByEmail(registerDTO.getEmail());

        if (existingUser.isPresent()) {
            return "Email already exists";
        }

        User user = new User();

        user.setFullName(registerDTO.getFullName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setRole(registerDTO.getRole());

        userRepository.save(user);

        return "User registered successfully";
    }

    // Login User
    public String loginUser(LoginDTO loginDTO) {

        Optional<User> userOptional =
                userRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isEmpty()) {
            return "User not found";
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(loginDTO.getPassword())) {
            return "Invalid password";
        }

        return "Login successful as " + user.getRole();
    }
}
