package com.meditrack.pharmacy.command;

import com.meditrack.pharmacy.service.SaleService;
import java.util.List;

public class GenerateBillCommand implements Command {

    private final SaleService saleService;
    private final String customerName;
    private final String phoneNumber;
    private final List<Integer> medicineIds;
    private final List<Integer> quantities;

    // Changed from Invoice to SaleResult
    private SaleService.SaleResult result;

    public GenerateBillCommand(
            SaleService saleService,
            String customerName,
            String phoneNumber,
            List<Integer> medicineIds,
            List<Integer> quantities
    ) {
        this.saleService  = saleService;
        this.customerName = customerName;
        this.phoneNumber  = phoneNumber;
        this.medicineIds  = medicineIds;
        this.quantities   = quantities;
    }

    @Override
    public void execute() {
        result = saleService.createSale(
                customerName,
                phoneNumber,
                medicineIds,
                quantities
        );
    }

    // -----------------------------------
    // Returns the full result wrapper
    // containing invoice + warnings
    // -----------------------------------
    public SaleService.SaleResult getResult() {
        return result;
    }
}