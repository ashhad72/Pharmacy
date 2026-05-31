package com.sda.pharmacy.controller;

import com.sda.pharmacy.entity.Medicine;
import com.sda.pharmacy.service.MedicineService;
import com.sda.pharmacy.command.CommandInvoker;
import com.sda.pharmacy.command.AddMedicineCommand;
import com.sda.pharmacy.command.DeleteMedicineCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MedicineController {

    @Autowired
    private CommandInvoker commandInvoker;

    @Autowired
    private MedicineService medicineService;

    // Show Medicines Page
    @GetMapping("/medicines")
    public String showMedicines(
            Model model,
            @RequestParam(value = "openModal", required = false) String openModal) {

        model.addAttribute(
                "medicines",
                medicineService.getAllMedicines()
        );

        model.addAttribute("openModal", openModal != null);

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
    // -----------------------------------
// GET /api/medicines/suggestions?q=Pa
// -----------------------------------

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(
            @RequestParam("q") String query) {

        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }

        List<String> suggestions = medicineService.getSuggestions(query);
        return ResponseEntity.ok(suggestions);
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
    public String getLowStockMedicines(Model model) {
        model.addAttribute("medicines", medicineService.getLowStockMedicines());
        model.addAttribute("currentView", "low-stock"); // ← make sure this is here
        return "medicine-inventory";
    }

    @GetMapping("/medicines/expired")
    public String getExpiredMedicines(Model model) {
        model.addAttribute("medicines", medicineService.getExpiredMedicines());
        model.addAttribute("currentView", "expired"); // ← and this
        return "medicine-inventory";
    }
    // Controller
    @GetMapping("/medicines/category/{type}")
    public String getMedicinesByCategory(@PathVariable String type, Model model) {
        // Fetches the filtered dataset and passes it directly to your existing inventory UI array
        model.addAttribute("medicines", medicineService.getMedicinesByCategory(type));
        return "medicine-inventory";
    }
// -----------------------------------
// Low Stock by Category
// -----------------------------------

    @GetMapping("/medicines/low-stock/category/{type}")
    public String getLowStockByCategory(
            @PathVariable String type,
            Model model) {

        model.addAttribute(
                "medicines",
                medicineService.getLowStockByCategory(type)
        );
        model.addAttribute("currentView", "low-stock");
        model.addAttribute("type", type);

        return "medicine-inventory";
    }

// -----------------------------------
// Expired by Category
// -----------------------------------

    @GetMapping("/medicines/expired/category/{type}")
    public String getExpiredByCategory(
            @PathVariable String type,
            Model model) {

        model.addAttribute(
                "medicines",
                medicineService.getExpiredByCategory(type)
        );
        model.addAttribute("currentView", "expired");
        model.addAttribute("type", type);

        return "medicine-inventory";
    }
    // Service + Repository

}