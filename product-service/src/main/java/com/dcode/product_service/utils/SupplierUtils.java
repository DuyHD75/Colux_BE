package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.SupplierRequest;
import com.dcode.product_service.dtoResponse.SupplierResponse;
import com.dcode.product_service.entity.ProductSupplier;

import java.util.UUID;

public class SupplierUtils {
    public static ProductSupplier fromSupplierEntity(SupplierRequest supplierRequest){
        return ProductSupplier.builder()
                .supplierId(UUID.randomUUID().toString())
                .name(supplierRequest.getName())
                .code(supplierRequest.getCode())
                .phone(supplierRequest.getPhone())
                .email(supplierRequest.getEmail())
                .build();
    }
    public static SupplierResponse fromSupplierEntityToResponse(ProductSupplier productSupplier){
        return SupplierResponse.builder()
                .supplierId(productSupplier.getSupplierId())
                .name(productSupplier.getName())
                .code(productSupplier.getCode())
                .phone(productSupplier.getPhone())
                .email(productSupplier.getEmail())
                .build();
    }
}
