package com.dcode.order_service.dto.order.response;

public record OrderLineResponse(
        Long id,
        String productId,
        String variantId,
        String paintId,
        String wallpaperId,
        String floorId,
        double quantity
) {
}
