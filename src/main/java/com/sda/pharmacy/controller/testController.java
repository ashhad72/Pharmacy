package com.sda.pharmacy.controller;


import com.sda.pharmacy.entity.testEntity;
import com.sda.pharmacy.repository.testRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class testController {

    @Autowired
    private testRepo medicineRepository;

    @GetMapping("/test")
    public List<testEntity> testConnection() {
        return medicineRepository.findAll();
    }
}
