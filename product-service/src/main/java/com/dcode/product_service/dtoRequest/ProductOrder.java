package com.dcode.product_service.dtoRequest;

import lombok.Data;

@Data
public class ProductOrder {
    private String productId;
    private String variantId;
    private String colorId;
    private Double quantity;
    private boolean success;
}
