package com.sda.pharmacy.service;

import com.sda.pharmacy.entity.Medicine;
import com.sda.pharmacy.factory.MedicineFactory;
import com.sda.pharmacy.singleton.SystemLogger;

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

        SystemLogger.getInstance()
                .log(
                        "MEDICINE",
                        "Starting medicine addition process."
                );

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
        SystemLogger.getInstance()
                .log(
                        "MEDICINE",
                        "Medicine object created using Factory Pattern."
                );

        AbstractSupplier supplier =

                SupplierFactory.getSupplier(
                        newMedicine.getSupplier()
                );
        SystemLogger.getInstance()
                .log(
                        "MEDICINE",
                        "Supplier resolved successfully."
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

        SystemLogger.getInstance()
                .log(
                        "MEDICINE",
                        "Add medicine procedure executed successfully."
                );

        // Notify Observers

        inventoryManager.notifyObservers();

        SystemLogger.getInstance()
                .log(
                        "INVENTORY",
                        "Inventory observers notified."
                );

        SystemLogger.getInstance()
                .log(
                        "INVENTORY",
                        "Medicine added successfully: "
                                + newMedicine.getMedicineName()
                );

        return "Medicine Added Successfully";
    }

    // -----------------------------------
    // Delete Medicine
    // + Observer Notification
    // -----------------------------------

    public String deleteMedicine(int id) {
        SystemLogger.getInstance()
                .log(
                        "MEDICINE",
                        "Deleting medicine with ID: " + id
                );

        medicineRepository.deleteById(id);

        SystemLogger.getInstance()
                .log(
                        "INVENTORY",
                        "Medicine deleted successfully."
                );

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

        SystemLogger.getInstance()
                .log(
                        "INVENTORY",
                        "Medicine search performed for keyword: "
                                + keyword
                );

        return medicineRepository
                .searchMedicineProcedure(keyword);
    }

    // -----------------------------------
    // Low Stock Medicines Procedure
    // -----------------------------------

    public List<Medicine> getLowStockMedicines() {

        SystemLogger.getInstance()
                .log(
                        "INVENTORY",
                        "Fetching low stock medicines."
                );

        return medicineRepository
                .getLowStockMedicines();
    }

    // -----------------------------------
    // Expired Medicines Procedure
    // -----------------------------------

    public List<Medicine> getExpiredMedicines() {

        SystemLogger.getInstance()
                .log(
                        "INVENTORY",
                        "Fetching expired medicines."
                );

        return medicineRepository
                .getExpiredMedicines();
    }

    // -----------------------------------
    // Dashboard SQL Functions
    // -----------------------------------

    public int getMedicineCount() {

        SystemLogger.getInstance()
                .log(
                        "DASHBOARD",
                        "Fetching medicine count."
                );

        return medicineRepository
                .getMedicineCount();
    }

    public int getLowStockCount() {

        SystemLogger.getInstance()
                .log(
                        "DASHBOARD",
                        "Fetching low stocks medicine count"
                );

        return medicineRepository
                .getLowStockCount();
    }

    public int getExpiredMedicineCount() {

        SystemLogger.getInstance()
                .log(
                        "DASHBOARD",
                        "Fetching Expired medicine count"
                );

        return medicineRepository
                .getExpiredMedicineCount();
    }

    public double getTotalStockValue() {

        SystemLogger.getInstance()
                .log(
                        "DASHBOARD",
                        "Fetching total Inventory Value"
                );

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