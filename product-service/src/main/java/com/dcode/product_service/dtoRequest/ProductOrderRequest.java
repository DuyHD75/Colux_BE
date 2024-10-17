package com.dcode.product_service.dtoRequest;

import lombok.Data;

@Data
public class ProductOrderRequest {
    private String paintId;
    private String wallpaperId;
    private String floorId;
    private String variantId;
    private Double quantity;

}
