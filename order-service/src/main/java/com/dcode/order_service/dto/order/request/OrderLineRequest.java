package com.dcode.order_service.dto.order.request;

public record OrderLineRequest(
        String orderId,
        String productId,
        String variantId,
        String colorId,
        double quantity
) {
}
