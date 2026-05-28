package com.sda.pharmacy.repository;

import com.sda.pharmacy.entity.SaleItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository
        extends JpaRepository<SaleItem, Integer> {

}