package com.dcode.product_service.dtoRequest;

import lombok.Data;

@Data
public class ProductOrder {
    private String productId;
    private String productType;
    private int quantity;
    private boolean success;
}
