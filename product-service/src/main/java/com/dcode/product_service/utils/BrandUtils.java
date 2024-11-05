package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.BrandResponse;
import com.dcode.product_service.entity.Brand;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BrandUtils {

    public static Brand createNewBrandEntity(String name, String code, String status){
        return Brand.builder()
                .brandId(UUID.randomUUID().toString())
                .code(code)
                .name(name)
                .status(status)
                .build();
    }
    public static Set<BrandResponse> fromEntityToResponse(Set<Brand> brands){
        return brands.stream()
                .map(brand -> BrandResponse.builder()
                        .brandId(brand.getBrandId())
                        .code(brand.getCode())
                        .name(brand.getName())
                        .status(brand.getStatus())
                        .build())
                .collect(Collectors.toSet());
    }
}
