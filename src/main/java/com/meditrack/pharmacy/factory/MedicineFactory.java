package com.meditrack.pharmacy.factory;


import com.meditrack.pharmacy.entity.Category;
import com.meditrack.pharmacy.entity.Medicine;
import com.meditrack.pharmacy.entity.Supplier;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicineFactory {

    public static Medicine createMedicine(
            String medicineName,
            Category category,
            Supplier supplier,
            String batchNumber,
            BigDecimal price,
            int quantityInStock,
            LocalDate manufactureDate,
            LocalDate expiryDate,
            String description
    ) {

        Medicine medicine = new Medicine();

        medicine.setMedicineName(medicineName);
        medicine.setCategory(category);
        medicine.setSupplier(supplier);
        medicine.setBatchNumber(batchNumber);
        medicine.setPrice(price);
        medicine.setQuantityInStock(quantityInStock);
        medicine.setManufactureDate(manufactureDate);
        medicine.setExpiryDate(expiryDate);
        medicine.setDescription(description);

        return medicine;
    }
}


