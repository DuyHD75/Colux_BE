package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.SupplierRequest;

public interface ISupplierService {
    void createSupplier(SupplierRequest supplierRequest);

    Object getAllSuppliers();
}
