package com.sda.pharmacy.controller;

import com.sda.pharmacy.entity.Medicine;
import com.sda.pharmacy.service.MedicineService;
import com.sda.pharmacy.command.CommandInvoker;
import com.sda.pharmacy.command.AddMedicineCommand;
import com.sda.pharmacy.command.DeleteMedicineCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MedicineController {

    @Autowired
    private CommandInvoker commandInvoker;

    @Autowired
    private MedicineService medicineService;

    // Show Medicines Page
    @GetMapping("/medicines")
    public String showMedicines(Model model) {

        model.addAttribute(
                "medicines",
                medicineService.getAllMedicines()
        );

        return "medicine-inventory";
    }

    // Add Medicine
    @PostMapping("/medicines/add")
    public String addMedicine(@ModelAttribute Medicine medicine) {

        AddMedicineCommand command =

                new AddMedicineCommand(
                        medicineService,
                        medicine
                );

        commandInvoker.executeCommand(command);

        return "redirect:/medicines";
    }

    // Delete Medicine
    @GetMapping("/medicines/delete/{id}")
    public String deleteMedicine(@PathVariable int id) {

        DeleteMedicineCommand command =

                new DeleteMedicineCommand(
                        medicineService,
                        id
                );

        commandInvoker.executeCommand(command);

        return "redirect:/medicines";
    }

    // Search Medicine
    @GetMapping("/medicines/search")
    public String searchMedicine(
            @RequestParam String keyword,
            Model model) {

        model.addAttribute(
                "medicines",
                medicineService.searchMedicine(keyword)
        );

        return "medicine-inventory";
    }

    // Low Stock Medicines
    @GetMapping("/medicines/low-stock")
    public String lowStockMedicines(Model model) {

        model.addAttribute(
                "medicines",
                medicineService.getLowStockMedicines()
        );

        return "medicine-inventory";
    }

    // Expired Medicines
    @GetMapping("/medicines/expired")
    public String expiredMedicines(Model model) {

        model.addAttribute(
                "medicines",
                medicineService.getExpiredMedicines()
        );

        return "medicine-inventory";
    }
}