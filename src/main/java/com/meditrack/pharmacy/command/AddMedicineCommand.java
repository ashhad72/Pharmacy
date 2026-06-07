package com.meditrack.pharmacy.command;

import com.meditrack.pharmacy.entity.Medicine;
import com.meditrack.pharmacy.service.MedicineService;

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
