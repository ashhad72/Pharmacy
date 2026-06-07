package com.meditrack.pharmacy.factory;

import com.meditrack.pharmacy.entity.Supplier;
import com.meditrack.pharmacy.nullobject.AbstractSupplier;
import com.meditrack.pharmacy.nullobject.NullSupplier;
import com.meditrack.pharmacy.nullobject.RealSupplier;

public class SupplierFactory {

    public static AbstractSupplier getSupplier(Supplier supplier) {

        if (supplier == null) {

            return new NullSupplier();
        }

        return new RealSupplier(supplier);
    }
}