package com.dcode.product_service.dtoRequest;

import lombok.Data;

@Data
public class VariantAttributeRequest {
    private String sizeName;
    private String categoryName;
    private String packageType;
}
