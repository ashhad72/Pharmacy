package com.sda.pharmacy.service;

import com.sda.pharmacy.entity.Medicine;
import com.sda.pharmacy.factory.MedicineFactory;

import com.sda.pharmacy.factory.SupplierFactory;
import com.sda.pharmacy.nullobject.AbstractSupplier;
import com.sda.pharmacy.observer.ExpiryObserver;
import com.sda.pharmacy.observer.InventoryManager;
import com.sda.pharmacy.observer.LowStockObserver;

import com.sda.pharmacy.repository.MedicineRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    private final InventoryManager inventoryManager;

    // -----------------------------------
    // Constructor Injection
    // -----------------------------------

    @Autowired
    public MedicineService(

            MedicineRepository medicineRepository,
            InventoryManager inventoryManager,
            LowStockObserver lowStockObserver,
            ExpiryObserver expiryObserver

    ) {

        this.medicineRepository = medicineRepository;

        this.inventoryManager = inventoryManager;

        inventoryManager.addObserver(lowStockObserver);

        inventoryManager.addObserver(expiryObserver);
    }

    // -----------------------------------
    // Add Medicine
    // Factory Pattern + Procedure
    // + Observer Notification
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

        AbstractSupplier supplier =

                SupplierFactory.getSupplier(
                        newMedicine.getSupplier()
                );

        medicineRepository.addMedicineProcedure(

                newMedicine.getMedicineName(),

                newMedicine.getCategory()
                        .getCategoryId(),

                supplier.getSupplierId(),

                newMedicine.getPrice(),

                newMedicine.getQuantityInStock(),

                Date.valueOf(
                        newMedicine.getExpiryDate()
                )
        );

        // Notify Observers

        inventoryManager.notifyObservers();

        return "Medicine Added Successfully";
    }

    // -----------------------------------
    // Delete Medicine
    // + Observer Notification
    // -----------------------------------

    public String deleteMedicine(int id) {

        medicineRepository.deleteById(id);

        return "Medicine deleted successfully";
    }

    // -----------------------------------
    // Get All Medicines
    // -----------------------------------

    public List<Medicine> getAllMedicines() {

        return medicineRepository.findAll();
    }

    // -----------------------------------
    // Search Medicine Procedure
    // -----------------------------------

    public List<Medicine> searchMedicine(String keyword) {

        return medicineRepository
                .searchMedicineProcedure(keyword);
    }

    // -----------------------------------
    // Low Stock Medicines Procedure
    // -----------------------------------

    public List<Medicine> getLowStockMedicines() {

        return medicineRepository
                .getLowStockMedicines();
    }

    // -----------------------------------
    // Expired Medicines Procedure
    // -----------------------------------

    public List<Medicine> getExpiredMedicines() {

        return medicineRepository
                .getExpiredMedicines();
    }

    // -----------------------------------
    // Dashboard SQL Functions
    // -----------------------------------

    public int getMedicineCount() {

        return medicineRepository
                .getMedicineCount();
    }

    public int getLowStockCount() {

        return medicineRepository
                .getLowStockCount();
    }

    public int getExpiredMedicineCount() {

        return medicineRepository
                .getExpiredMedicineCount();
    }

    public double getTotalStockValue() {

        return medicineRepository
                .getTotalStockValue();
    }

    // -----------------------------------
    // Database View Integration
    // -----------------------------------

    public List<Object[]> getMedicineStockView() {

        return medicineRepository
                .getMedicineStockView();
    }
}