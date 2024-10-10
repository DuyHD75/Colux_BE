package com.dcode.order_service.dto.cart.request;

import lombok.Data;

@Data
public class CartVariantRequest {
    private String variantId;
    private Integer quantity;
}
