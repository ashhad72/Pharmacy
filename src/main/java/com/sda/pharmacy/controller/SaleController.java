package com.sda.pharmacy.controller;

import com.sda.pharmacy.builder.Invoice;
import com.sda.pharmacy.service.SaleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SaleController {

    @Autowired
    private SaleService saleService;

    // Open Billing Page

    @GetMapping("/billing")
    public String billingPage() {

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

        Invoice invoice =
                saleService.createSale(

                        customerName,
                        phoneNumber,

                        medicineId,
                        quantity
                );

        model.addAttribute(
                "invoice",
                invoice
        );

        return "invoice";
    }

    @GetMapping("/sales/reports")

    public String salesReports(Model model) {

        model.addAttribute(
                "reports",
                saleService.getSalesReports()
        );

        return "sales-report";
    }
}