package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.VariantResponse;
import com.dcode.product_service.entity.Variant;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class VariantUtils {
    public static Set<VariantResponse> fromVariantEntity(Set<Variant> variant){
        return variant.stream().map(
                v -> { VariantResponse variantResponse = new VariantResponse();
                    variantResponse.setCategoryName(v.getCategoryName());
                    variantResponse.setSizeName(v.getSizeName());
                    variantResponse.setPackageType(v.getPackageType());
                    return variantResponse;
                }).collect(Collectors.toSet());
    }
    public static Variant fromAVariantEntity(String sizeName, String categoryName, String packageType){
        return Variant.builder()
                .variantId(UUID.randomUUID().toString())
                .sizeName(sizeName)
                .categoryName(categoryName)
                .packageType(packageType)
                .build();
    }

}
