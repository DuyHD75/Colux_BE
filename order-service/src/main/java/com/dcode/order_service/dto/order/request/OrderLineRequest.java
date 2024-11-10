package com.dcode.order_service.dto.order.request;

import java.math.BigDecimal;

public record OrderLineRequest(
        String orderId,
        String productId,
        Integer quantity,
        BigDecimal trackingPrice,
        String variantId,
        String paintId,
        String wallpaperId,
        String floorId
) {
}
