package com.dcode.product_service.dtoResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VariantResponse {
    private String variantId;
    private String sizeName;
    private String categoryName;
    private String packageType;
    private String quantity;
}
