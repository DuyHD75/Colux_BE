package com.dcode.product_service.dtoRequest;

import lombok.Data;

import java.util.Set;

@Data
public class ProductRequest {
    private String description;
    private String placeOfOrigin;
    private String price;
    private String productName;
    private String ratingAverage;
    private String code;
    private String warranty;
    private String applicableSurface;
    private String brandId;
    private String categoryId;
    private Set<String> featureValueIds;
    private Set<String> propertyValueIds;
}
