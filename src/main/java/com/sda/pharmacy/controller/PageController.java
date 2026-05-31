package com.sda.pharmacy.controller;
import com.sda.pharmacy.entity.Medicine;
import com.sda.pharmacy.service.MedicineService;
import com.sda.pharmacy.service.SaleService;
import org.springframework.ui.Model;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // Inventory Lists (for table, alerts, activity log)
        // -----------------------------

        List<Medicine> allMedicines = medicineService.getAllMedicines();
        List<Medicine> lowStockList = medicineService.getLowStockMedicines();
        List<Medicine> expiredList  = medicineService.getExpiredMedicines();

        model.addAttribute("medicines",         allMedicines);
        model.addAttribute("lowStockMedicines", lowStockList);
        model.addAttribute("expiredMedicines",  expiredList);

        // -----------------------------
        // Category Chart Data
        // -----------------------------

        Map<String, Integer> catMap = allMedicines.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getCategory().getCategoryName(),
                        Collectors.summingInt(Medicine::getQuantityInStock)
                ));

        model.addAttribute("categoryLabels", new ArrayList<>(catMap.keySet()));
        model.addAttribute("categoryValues", new ArrayList<>(catMap.values()));

        // -----------------------------
        // Sales Statistics
        // -----------------------------

        BigDecimal todayRevenue    = saleService.getTodayRevenue();
        BigDecimal monthlyRevenue  = saleService.getMonthlyRevenue();
        BigDecimal lastMonthRevenue= saleService.getLastMonthRevenue();
        int todayTxCount           = saleService.getTodayTransactionCount();

        model.addAttribute("todayRevenue",     todayRevenue);
        model.addAttribute("monthlyRevenue",   monthlyRevenue);
        model.addAttribute("lastMonthRevenue", lastMonthRevenue);
        model.addAttribute("todayTxCount",     todayTxCount);
        model.addAttribute("totalRevenue",     saleService.getTotalRevenue());

        // Monthly trend % change
        double trend = 0;
        if (lastMonthRevenue != null && lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            trend = monthlyRevenue.subtract(lastMonthRevenue)
                    .divide(lastMonthRevenue, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        model.addAttribute("revenueTrend", String.format("%.1f", trend));
        model.addAttribute("revenueTrendUp", trend >= 0);

        // -----------------------------
        // Recent Transactions
        // -----------------------------

        model.addAttribute("recentSales", saleService.getRecentSales());

        // -----------------------------
        // Monthly Chart Data (JSON arrays for Chart.js)
        // -----------------------------

        List<Object[]> chartData = saleService.getMonthlyRevenueChart();

        // Build full 12-month array, filling 0 for months with no sales
        double[] monthlyChartValues = new double[12];
        for (Object[] row : chartData) {
            int monthIndex = ((Number) row[0]).intValue() - 1; // 0-indexed
            double revenue = ((Number) row[1]).doubleValue();
            monthlyChartValues[monthIndex] = revenue;
        }

        // Convert to JSON string for the template
        StringBuilder chartJson = new StringBuilder("[");
        for (int i = 0; i < 12; i++) {
            chartJson.append(monthlyChartValues[i]);
            if (i < 11) chartJson.append(",");
        }
        chartJson.append("]");

        model.addAttribute("monthlyChartData", chartJson.toString());

        return "dashboard";
    }

}