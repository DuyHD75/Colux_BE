package com.dcode.product_service.service;


import com.dcode.product_service.dtoResponse.VariantResponse;

import java.util.Set;

public interface IVariantService {
    void createAVariant(String sizeName, String categoryName, String packageType);

    Set<VariantResponse> getAllVariant();

//    Set<VariantResponse> getAllPaintVariant();
}
