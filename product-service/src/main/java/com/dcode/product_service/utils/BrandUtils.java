package com.dcode.product_service.utils;

import com.dcode.product_service.entity.Brand;

import java.util.UUID;

public class BrandUtils {

    public static Brand createNewBrandEntity(String name, String code, String status){
        return Brand.builder()
                .brandId(UUID.randomUUID().toString())
                .code(code)
                .name(name)
                .status(status)
                .build();
    }
}
