package com.dcode.product_service.dtoRequest;

import lombok.Data;

@Data
public class VariantRequest {
    private String sizeName;
    private String categoryName;
    private String packageType;
}
