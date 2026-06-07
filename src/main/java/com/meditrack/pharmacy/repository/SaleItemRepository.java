package com.meditrack.pharmacy.repository;

import com.meditrack.pharmacy.entity.SaleItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository
        extends JpaRepository<SaleItem, Integer> {

}