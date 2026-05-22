package com.sda.pharmacy.repository;


import com.sda.pharmacy.entity.testEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface testRepo extends JpaRepository<testEntity, Long> {
}
