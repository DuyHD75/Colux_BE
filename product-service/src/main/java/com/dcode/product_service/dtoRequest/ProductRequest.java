package com.dcode.product_service.dtoRequest;

import lombok.Data;

@Data
public class ProductRequest {
    private String description;
    private String placeOfOrigin;
    private String price;
    private String productName;
    private String ratingAverage;
    private String warranty;
    private String brandId;
    private String categoryId;

}
