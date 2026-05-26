package com.sda.pharmacy.command;

import com.sda.pharmacy.builder.Invoice;
import com.sda.pharmacy.service.SaleService;

public class GenerateBillCommand implements Command {

    private final SaleService saleService;

    private final String customerName;
    private final String phoneNumber;

    private final int medicineId;
    private final int quantity;

    private Invoice invoice;

    public GenerateBillCommand(

            SaleService saleService,

            String customerName,
            String phoneNumber,

            int medicineId,
            int quantity
    ) {

        this.saleService = saleService;

        this.customerName = customerName;
        this.phoneNumber = phoneNumber;

        this.medicineId = medicineId;
        this.quantity = quantity;
    }

    @Override
    public void execute() {

        invoice = saleService.createSale(

                customerName,
                phoneNumber,

                medicineId,
                quantity
        );
    }

    public Invoice getInvoice() {

        return invoice;
    }
}