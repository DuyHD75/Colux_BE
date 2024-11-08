package com.dcode.order_service.dto.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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

