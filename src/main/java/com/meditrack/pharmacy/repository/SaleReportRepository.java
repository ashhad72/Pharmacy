package com.meditrack.pharmacy.repository;

import com.meditrack.pharmacy.entity.SalesReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface SaleReportRepository extends JpaRepository<SalesReport, Integer> {
    @Query(value = "SELECT GetTotalRevenue()",
            nativeQuery = true)
    BigDecimal getTotalRevenue();

}