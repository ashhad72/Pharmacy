package com.sda.pharmacy.repository;


import com.sda.pharmacy.entity.Medicine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface MedicineRepository
        extends JpaRepository<Medicine, Integer> {

    // -----------------------------
    // STORED PROCEDURE
    // Add Medicine
    // -----------------------------

    @Procedure(procedureName = "AddMedicine")
    void addMedicineProcedure(
            @Param("p_medicine_name") String medicineName,
            @Param("p_category_id") int categoryId,
            @Param("p_supplier_id") int supplierId,
            @Param("p_price") BigDecimal price,
            @Param("p_quantity") int quantity,
            @Param("p_expiry_date") Date expiryDate
    );

    // -----------------------------
    // SEARCH MEDICINE PROCEDURE
    // -----------------------------

    @Query(
            value = "CALL SearchMedicine(:keyword)",
            nativeQuery = true
    )
    List<Medicine> searchMedicineProcedure(
            @Param("keyword") String keyword
    );

    // -----------------------------
    // LOW STOCK PROCEDURE
    // -----------------------------

    @Query(
            value = "CALL LowStockMedicines()",
            nativeQuery = true
    )
    List<Medicine> getLowStockMedicines();

    // -----------------------------
    // EXPIRED MEDICINES PROCEDURE
    // -----------------------------

    @Query(
            value = "CALL ExpiredMedicines()",
            nativeQuery = true
    )
    List<Medicine> getExpiredMedicines();

    // -----------------------------
    // VIEW INTEGRATION
    // -----------------------------

    @Query(
            value = "SELECT * FROM Medicine_Stock_View",
            nativeQuery = true
    )
    List<Object[]> getMedicineStockView();

    // -----------------------------
    // FUNCTIONS
    // -----------------------------

    @Query(
            value = "SELECT GetMedicineCount()",
            nativeQuery = true
    )
    int getMedicineCount();

    @Query(
            value = "SELECT GetLowStockCount()",
            nativeQuery = true
    )
    int getLowStockCount();

    @Query(
            value = "SELECT GetExpiredMedicineCount()",
            nativeQuery = true
    )
    int getExpiredMedicineCount();

    @Query(
            value = "SELECT GetTotalStockValue()",
            nativeQuery = true
    )
    double getTotalStockValue();
}