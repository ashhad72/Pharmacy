package com.sda.pharmacy.nullobject;

public class NullSupplier
        extends AbstractSupplier {

    @Override
    public int getSupplierId() {

        return 0;
    }

    @Override
    public String getSupplierName() {

        return "No Supplier Assigned";
    }

    @Override
    public String getContactNumber() {

        return "N/A";
    }

    @Override
    public String getEmail() {

        return "N/A";
    }

    @Override
    public String getAddress() {

        return "N/A";
    }

    @Override
    public boolean isNull() {

        return true;
    }
}