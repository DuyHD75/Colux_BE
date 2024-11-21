package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.BrandRequest;
import com.dcode.product_service.dtoResponse.BrandResponse;

import java.util.List;
import java.util.Set;

public interface IBrandService {
    void createBrand(String name, String code, String status);

    Set<BrandResponse> getAllBrands();

    void createBrands(List<BrandRequest> brandRequest);
}
