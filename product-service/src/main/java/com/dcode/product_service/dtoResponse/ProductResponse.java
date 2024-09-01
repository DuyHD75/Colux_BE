package com.dcode.product_service.dtoResponse;

import com.dcode.product_service.dto.ProductBrandDTO;
import com.dcode.product_service.dto.ProductCategoryDTO;
import com.dcode.product_service.entity.Brand;
import com.dcode.product_service.entity.Category;
import lombok.Data;

@Data
public class ProductResponse {
    private String productId;
    private String createdAt;
    private String createdBy;
    private String updateAt;
    private String updatedBy;
    private String description;
    private String placeOfOrigin;
    private String price;
    private String productName;
    private String ratingAverage;
    private String warranty;
    private ProductCategoryDTO category;
    private ProductBrandDTO brand;
}
