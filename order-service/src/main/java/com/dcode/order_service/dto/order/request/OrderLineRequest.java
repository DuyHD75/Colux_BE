package com.dcode.order_service.dto.order.request;

public record OrderLineRequest(
        Long id,
        String orderId,
        String productId,
        double quantity
) {
}
