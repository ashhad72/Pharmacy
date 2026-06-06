package com.meditrack.pharmacy.service;

import com.meditrack.pharmacy.entity.Medicine;
import com.meditrack.pharmacy.exception.MedicineDeletionException;
import com.meditrack.pharmacy.factory.MedicineFactory;
import com.meditrack.pharmacy.singleton.SystemLogger;

import com.meditrack.pharmacy.factory.SupplierFactory;
import com.meditrack.pharmacy.nullobject.AbstractSupplier;
import com.meditrack.pharmacy.observer.ExpiryObserver;
import com.meditrack.pharmacy.observer.InventoryManager;
import com.meditrack.pharmacy.observer.LowStockObserver;

import com.meditrack.pharmacy.repository.MedicineRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
                ),
                newMedicine.getBatchNumber()
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

        try {

            medicineRepository.deleteById(id);

        } catch (DataIntegrityViolationException ex) {

            throw new MedicineDeletionException(
                    "Cannot delete medicine because it exists in sales records."
            );
        }

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
// -----------------------------------
// Low Stock by Category
// -----------------------------------

    public List<Medicine> getLowStockByCategory(String type) {

        SystemLogger.getInstance()
                .log(
                        "INVENTORY",
                        "Fetching low stock medicines for category: " + type
                );

        return medicineRepository
                .getLowStockByCategory(type);
    }

// -----------------------------------
// Expired by Category
// -----------------------------------

    public List<Medicine> getExpiredByCategory(String type) {

        SystemLogger.getInstance()
                .log(
                        "INVENTORY",
                        "Fetching expired medicines for category: " + type
                );

        return medicineRepository
                .getExpiredByCategory(type);
    }
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
    // -----------------------------------
// Autocomplete Suggestions
// -----------------------------------

    public List<String> getSuggestions(String prefix) {

        SystemLogger.getInstance()
                .log(
                        "INVENTORY",
                        "Fetching suggestions for prefix: " + prefix
                );

        if (prefix == null || prefix.trim().isEmpty()) {
            return List.of();
        }

        return medicineRepository.getSuggestions(prefix.trim());
    }
    public Medicine findByName(String medicineName) {
        if (medicineName == null || medicineName.trim().isEmpty()) {
            return null;
        }

        // Calls your repository layer method
        return medicineRepository.findByMedicineNameIgnoreCase(medicineName.trim());
    }

    public List<Medicine> getMedicinesByCategory(String type) {
        SystemLogger.getInstance()
                .log(
                        "INVENTORY",
                        "Filtering medicine stock catalog by category type: " + type
                );

        return medicineRepository.findMedicinesByCategoryName(type);
    }

}