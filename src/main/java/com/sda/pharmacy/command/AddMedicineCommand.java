package com.sda.pharmacy.command;

import com.sda.pharmacy.entity.Medicine;
import com.sda.pharmacy.service.MedicineService;

public class AddMedicineCommand implements Command {

    private final MedicineService medicineService;

    private final Medicine medicine;

    public AddMedicineCommand(

            MedicineService medicineService,
            Medicine medicine
    ) {

        this.medicineService = medicineService;
        this.medicine = medicine;
    }

    @Override
    public void execute() {

        medicineService.addMedicine(medicine);

    }
}