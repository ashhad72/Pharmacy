package com.sda.pharmacy.command;

import com.sda.pharmacy.service.MedicineService;

public class DeleteMedicineCommand implements Command {

    private final MedicineService medicineService;

    private final int medicineId;

    public DeleteMedicineCommand(

            MedicineService medicineService,
            int medicineId
    ) {

        this.medicineService = medicineService;
        this.medicineId = medicineId;
    }

    @Override
    public void execute() {

        medicineService.deleteMedicine(medicineId);

    }
}