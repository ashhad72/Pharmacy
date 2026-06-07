package com.meditrack.pharmacy.controller;

import com.meditrack.pharmacy.builder.Invoice;
import com.meditrack.pharmacy.command.CommandInvoker;
import com.meditrack.pharmacy.entity.Medicine;
import com.meditrack.pharmacy.exception.InsufficientStockException;
import com.meditrack.pharmacy.service.SaleService;
import com.meditrack.pharmacy.service.MedicineService; // 1. IMPORT YOUR MEDICINE SERVICE
import com.meditrack.pharmacy.command.GenerateBillCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SaleController {

    @Autowired
    private CommandInvoker commandInvoker;

    @Autowired
    private SaleService saleService;

    @Autowired
    private MedicineService medicineService; // 2. INJECT THE MEDICINE SERVICE

    // Open Billing Page with Medicine Data Loaded
    @GetMapping("/billing")
    public String billingPage(Model model) { // 3. ADD MODEL PARAMETER HERE

        // 4. FETCH AND ATTACH MEDICINES TO THE MODEL
        // (Make sure your medicineService has a method like getAllMedicines() or findAll())
        model.addAttribute("medicines", medicineService.getAllMedicines());

        return "billing";
    }

    // Process Sale
    @PostMapping("/billing/create")
    public String createSale(
            @RequestParam String customerName,
            @RequestParam String phoneNumber,
            @RequestParam("medicineName") List<String> medicineNames, // ◄ Accepting multiple names from the checkout cart
            @RequestParam("quantity") List<Integer> quantities,       // ◄ Accepting matching list of quantities
            Model model
    ) {
        // Basic structural validation check
        if (medicineNames == null || quantities == null || medicineNames.size() != quantities.size() || medicineNames.isEmpty()) {
            model.addAttribute("errorMessage", "Error: Form data mismatch between selected medicines and quantities.");
            return "pos-counter";
        }

        // This array list will accumulate the converted numeric IDs
        List<Integer> medicineIds = new java.util.ArrayList<>();

        // 1. CONVERT THE MEDICINE NAME STRINGS INTO REAL DATABASE IDs
        for (String currentName : medicineNames) {
            Medicine medicine = medicineService.findByName(currentName);

            // 2. Error handling if any item typed/sent doesn't exist in system storage
            if (medicine == null) {
                model.addAttribute("errorMessage", "Error: The medicine '" + currentName + "' could not be found in stock.");
                return "pos-counter";
            }

            // 3. Pull the clean numeric database ID from your resolved entity and store it
            medicineIds.add(medicine.getMedicineId());
        }

        // Create Command using the complete list of extracted IDs and quantities
        GenerateBillCommand command = new GenerateBillCommand(
                saleService,
                customerName,
                phoneNumber,
                medicineIds, // ◄ Pass the complete converted ID list smoothly down
                quantities   // ◄ Pass the matching quantities list smoothly down
        );

        // Execute Command using your Invoker configuration pattern
        try {
            commandInvoker.executeCommand(command);

            SaleService.SaleResult result = command.getResult();

            model.addAttribute("invoice", result.invoice());

            if (result.hasWarnings()) {
                model.addAttribute("stockWarnings", result.warnings());
            }

            return "invoice";

        } catch (InsufficientStockException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("medicines", medicineService.getAllMedicines());
            return "billing";

        } catch (RuntimeException ex) {
            // Catches expired medicine block
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("medicines", medicineService.getAllMedicines());
            return "billing";
        }
    }

    @GetMapping("/sales/reports")
    public String salesReports(Model model) {
        model.addAttribute("reports", saleService.getSalesReports());

        // ADD THIS
        model.addAttribute("totalRevenue", saleService.getTotalRevenue());

        return "sales-report";
    }
}