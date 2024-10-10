package com.dcode.product_service.entity;

public interface OrderRequest {
    String getIdentity();
    String getProductId();
    String getVariantId();
    Double getQuantity();
}
