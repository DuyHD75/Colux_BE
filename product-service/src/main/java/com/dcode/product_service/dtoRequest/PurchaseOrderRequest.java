package com.dcode.product_service.dtoRequest;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderRequest {
//    private String orderId;
    private List<ProductOrderRequest> products;
}
