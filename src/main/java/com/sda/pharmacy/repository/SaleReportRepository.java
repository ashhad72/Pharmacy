package com.sda.pharmacy.repository;

import com.sda.pharmacy.entity.SalesReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface SaleReportRepository extends JpaRepository<SalesReport, Integer> {
    @Query(value = "SELECT GetTotalRevenue()",
            nativeQuery = true)
    BigDecimal getTotalRevenue();

}