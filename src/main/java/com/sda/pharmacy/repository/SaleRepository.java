package com.sda.pharmacy.repository;

import com.sda.pharmacy.entity.Sale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SaleRepository
        extends JpaRepository<Sale, Integer> {
// -----------------------------
// Today's Revenue
// -----------------------------

    @Query(
            value = "SELECT COALESCE(SUM(total_amount), 0) FROM sales WHERE DATE(sale_date) = CURDATE()",
            nativeQuery = true
    )
    BigDecimal getTodayRevenue();

// -----------------------------
// Today's Transaction Count
// -----------------------------

    @Query(
            value = "SELECT COUNT(*) FROM sales WHERE DATE(sale_date) = CURDATE()",
            nativeQuery = true
    )
    int getTodayTransactionCount();

// -----------------------------
// Monthly Revenue (current month)
// -----------------------------

    @Query(
            value = "SELECT COALESCE(SUM(total_amount), 0) FROM sales WHERE MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE())",
            nativeQuery = true
    )
    BigDecimal getMonthlyRevenue();

// -----------------------------
// Last Month Revenue (for trend)
// -----------------------------

    @Query(
            value = "SELECT COALESCE(SUM(total_amount), 0) FROM sales WHERE MONTH(sale_date) = MONTH(CURDATE() - INTERVAL 1 MONTH) AND YEAR(sale_date) = YEAR(CURDATE() - INTERVAL 1 MONTH)",
            nativeQuery = true
    )
    BigDecimal getLastMonthRevenue();

// -----------------------------
// Recent Sales (last 5) for transactions panel
// -----------------------------

    @Query(
            value = "SELECT * FROM sales ORDER BY sale_date DESC LIMIT 5",
            nativeQuery = true
    )
    List<Sale> getRecentSales();

// -----------------------------
// Monthly revenue grouped by month (for line chart)
// Returns [month_number, total_revenue]
// -----------------------------

    @Query(
            value = """
        SELECT MONTH(sale_date) AS month, COALESCE(SUM(total_amount), 0) AS revenue
        FROM sales
        WHERE YEAR(sale_date) = YEAR(CURDATE())
        GROUP BY MONTH(sale_date)
        ORDER BY MONTH(sale_date)
        """,
            nativeQuery = true
    )
    List<Object[]> getMonthlyRevenueChart();
}