package com.dcode.order_service.dto.cart.request;

import lombok.Data;

@Data
public class CartVariantRequest {
    private String variantId;
    private String productId;
    private String colorId;
    private String wallpaperId;
    private String floorId;
    private Integer quantity;
}
