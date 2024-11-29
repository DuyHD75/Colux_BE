package com.dcode.product_service.dtoRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VariantRequest {
    private String variantId;
    private Integer quantity;
    private Double price;
}
