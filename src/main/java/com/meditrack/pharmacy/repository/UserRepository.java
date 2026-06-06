package com.meditrack.pharmacy.repository;

import com.meditrack.pharmacy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Integer> {

    User findByFullName(String fullName);
}