package com.dcode.order_service.dto.order.request;

public record OrderLineRequest(
        String orderId,
        String productId,
        Double quantity,
        Double trackingPrice,
        String variantId,
        String paintId,
        String wallpaperId,
        String floorId
) {
}
