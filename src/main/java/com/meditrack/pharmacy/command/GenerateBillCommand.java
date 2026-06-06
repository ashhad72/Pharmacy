package com.meditrack.pharmacy.command;

import com.meditrack.pharmacy.builder.Invoice;
import com.meditrack.pharmacy.service.SaleService;
import java.util.List;

public class GenerateBillCommand implements Command {

    private final SaleService saleService;
    private final String customerName;
    private final String phoneNumber;

    // ── CHANGED FROM SINGLE PRIMITIVES TO LISTS ──
    private final List<Integer> medicineIds;
    private final List<Integer> quantities;

    private Invoice invoice;

    // Updated Constructor to handle Lists safely
    public GenerateBillCommand(
            SaleService saleService,
            String customerName,
            String phoneNumber,
            List<Integer> medicineIds,
            List<Integer> quantities
    ) {
        this.saleService = saleService;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.medicineIds = medicineIds;
        this.quantities = quantities;
    }

    @Override
    public void execute() {
        // Delegates the lists down to your service layer method
        invoice = saleService.createSale(
                customerName,
                phoneNumber,
                medicineIds,
                quantities
        );
    }

    public Invoice getInvoice() {
        return invoice;
    }
}