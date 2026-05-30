package com.sda.pharmacy.factory;

import com.sda.pharmacy.entity.Supplier;
import com.sda.pharmacy.nullobject.AbstractSupplier;
import com.sda.pharmacy.nullobject.NullSupplier;
import com.sda.pharmacy.nullobject.RealSupplier;

public class SupplierFactory {

    public static AbstractSupplier getSupplier(Supplier supplier) {

        if (supplier == null) {

            return new NullSupplier();
        }

        return new RealSupplier(supplier);
    }
}