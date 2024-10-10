package com.dcode.product_service.dtoRequest;

import lombok.Data;

@Data
public class VariantRequest {
    private String variantId;
    private Double quantity;
    private Double price;
}
