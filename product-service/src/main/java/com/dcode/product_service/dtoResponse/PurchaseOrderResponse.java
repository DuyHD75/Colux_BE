package com.dcode.product_service.dtoResponse;

import lombok.Data;

@Data
public class PurchaseOrderResponse {
    private String productId;
    private String productType;
    private int quantity;
    private boolean success;
}
