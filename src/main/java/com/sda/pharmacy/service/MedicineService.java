package com.sda.pharmacy.service;

import com.sda.pharmacy.entity.Medicine;
import com.sda.pharmacy.factory.MedicineFactory;
import com.sda.pharmacy.repository.MedicineRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    // -----------------------------------
    // Add Medicine using Factory + Procedure
    // -----------------------------------

    public String addMedicine(Medicine medicine) {

        Medicine newMedicine =
                MedicineFactory.createMedicine(
                        medicine.getMedicineName(),
                        medicine.getCategory(),
                        medicine.getSupplier(),
                        medicine.getBatchNumber(),
                        medicine.getPrice(),
                        medicine.getQuantityInStock(),
                        medicine.getManufactureDate(),
                        medicine.getExpiryDate(),
                        medicine.getDescription()
                );

        medicineRepository.addMedicineProcedure(
                newMedicine.getMedicineName(),
                newMedicine.getCategory().getCategoryId(),
                newMedicine.getSupplier().getSupplierId(),
                newMedicine.getPrice(),
                newMedicine.getQuantityInStock(),
                Date.valueOf(newMedicine.getExpiryDate())
        );

        return "Medicine Added Successfully";
    }

    // -----------------------------------
    // Normal CRUD
    // -----------------------------------

    public List<Medicine> getAllMedicines() {

        return medicineRepository.findAll();
    }

    public String deleteMedicine(int id) {

        medicineRepository.deleteById(id);

        return "Medicine deleted successfully";
    }

    // -----------------------------------
    // Search Procedure
    // -----------------------------------

    public List<Medicine> searchMedicine(String keyword) {

        return medicineRepository
                .searchMedicineProcedure(keyword);
    }

    // -----------------------------------
    // Low Stock Procedure
    // -----------------------------------

    public List<Medicine> getLowStockMedicines() {

        return medicineRepository.getLowStockMedicines();
    }

    // -----------------------------------
    // Expired Medicines Procedure
    // -----------------------------------

    public List<Medicine> getExpiredMedicines() {

        return medicineRepository.getExpiredMedicines();
    }

    // -----------------------------------
    // Dashboard Functions
    // -----------------------------------

    public int getMedicineCount() {

        return medicineRepository.getMedicineCount();
    }

    public int getLowStockCount() {

        return medicineRepository.getLowStockCount();
    }

    public int getExpiredMedicineCount() {

        return medicineRepository.getExpiredMedicineCount();
    }

    public double getTotalStockValue() {

        return medicineRepository.getTotalStockValue();
    }

    // -----------------------------------
    // View Integration
    // -----------------------------------

    public List<Object[]> getMedicineStockView() {

        return medicineRepository.getMedicineStockView();
    }
}