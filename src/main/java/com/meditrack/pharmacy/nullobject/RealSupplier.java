package com.meditrack.pharmacy.nullobject;

import com.meditrack.pharmacy.entity.Supplier;

public class RealSupplier
        extends AbstractSupplier {

    private final Supplier supplier;

    public RealSupplier(Supplier supplier) {

        this.supplier = supplier;
    }

    @Override
    public int getSupplierId() {

        return supplier.getSupplierId();
    }

    @Override
    public String getSupplierName() {

        return supplier.getSupplierName();
    }

    @Override
    public String getContactNumber() {

        return supplier.getContactNumber();
    }

    @Override
    public String getEmail() {

        return supplier.getEmail();
    }

    @Override
    public String getAddress() {

        return supplier.getAddress();
    }

    @Override
    public boolean isNull() {

        return false;
    }
}