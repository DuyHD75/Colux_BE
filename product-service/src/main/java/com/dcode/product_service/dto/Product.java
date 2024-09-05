package com.dcode.product_service.dto;

import lombok.Data;

@Data
public class Product {
    private Long Id;
    private String createdAt;
    private Long createdBy;
    private String updateAt;
    private Long updatedBy;
    private String description;
    private String placeOfOrigin;
    private String price;
    private String productName;
    private String ratingAverage;
    private String warranty;
    private String brandId;
    private String categoryId;
}
