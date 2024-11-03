package com.dcode.order_service.dto.cart.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CartVariantRequest {
    private String variantId;
    private String productId;
    private String paintId;
    private String wallpaperId;
    private String floorId;
    private Integer quantity;


    public CartVariantRequest(String productId, String variantId, String paintId, String wallpaperId, String floorId) {
        this.productId = productId;
        this.variantId = variantId;
        this.paintId = paintId;
        this.wallpaperId = wallpaperId;
        this.floorId = floorId;
    }
}
