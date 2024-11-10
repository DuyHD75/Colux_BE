package com.dcode.product_service.dtoRequest.order_service;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderLineDTO {
    private Long id;
    private String productId;
    private Integer quantity;
    private String variantId;
    private String paintId;
    private String wallpaperId;
    private String floorId;
//    private LocalDateTime createdAt;
}

