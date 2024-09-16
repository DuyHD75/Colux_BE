package com.dcode.order_service.dto.order.response;

public record OrderLineResponse(
        Long id,
        String productId,
        String variantId,
        String colorID,
        double quantity
) {
}
