package com.dcode.product_service.dtoResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductOrderResponse {
    private String paintId;
    private String wallpaperId;
    private String floorId;
    private String variantId;
    private Double quantity;
    private Double price;
    private String message;
    private boolean success;
}
