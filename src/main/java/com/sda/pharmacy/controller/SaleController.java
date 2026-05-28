package com.sda.pharmacy.controller;

import com.sda.pharmacy.builder.Invoice;
import com.sda.pharmacy.command.CommandInvoker;
import com.sda.pharmacy.service.SaleService;
import com.sda.pharmacy.service.MedicineService; // 1. IMPORT YOUR MEDICINE SERVICE
import com.sda.pharmacy.command.GenerateBillCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam int medicineId,
            @RequestParam int quantity,
            Model model
    ) {
        // Create Command
        GenerateBillCommand command =
                new GenerateBillCommand(
                        saleService,
                        customerName,
                        phoneNumber,
                        medicineId,
                        quantity
                );

        // Execute Command
        commandInvoker.executeCommand(command);

        // Get Generated Invoice
        Invoice invoice = command.getInvoice();

        // Send Invoice to Frontend
        model.addAttribute("invoice", invoice);

        return "invoice";
    }

    @GetMapping("/sales/reports")
    public String salesReports(Model model) {
        model.addAttribute("reports", saleService.getSalesReports());

        // ADD THIS
        model.addAttribute("totalRevenue", saleService.getTotalRevenue());

        return "sales-report";
    }
}