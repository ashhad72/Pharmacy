package com.sda.pharmacy.repository;

import com.sda.pharmacy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Integer> {

    User findByEmail(String email);
}