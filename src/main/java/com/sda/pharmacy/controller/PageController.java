package com.sda.pharmacy.controller;
import com.sda.pharmacy.service.MedicineService;
import com.sda.pharmacy.service.SaleService;
import org.springframework.ui.Model;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {


    @Autowired
    private MedicineService medicineService;

    @Autowired
    private SaleService saleService;

    // Landing Page
    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    // Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Main dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // -----------------------------
        // Medicine Statistics
        // -----------------------------

        model.addAttribute(
                "medicineCount",
                medicineService.getMedicineCount()
        );

        model.addAttribute(
                "lowStockCount",
                medicineService.getLowStockCount()
        );

        model.addAttribute(
                "expiredCount",
                medicineService.getExpiredMedicineCount()
        );

        model.addAttribute(
                "totalStockValue",
                medicineService.getTotalStockValue()
        );

        // -----------------------------
        // Sales Statistics
        // -----------------------------

        model.addAttribute(
                "totalRevenue",
                saleService.getTotalRevenue()
        );

        return "dashboard";
    }

}
