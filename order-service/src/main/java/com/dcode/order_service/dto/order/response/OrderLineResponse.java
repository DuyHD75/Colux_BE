package com.dcode.order_service.dto.order.response;

public record OrderLineResponse(
        Long id,
        String productId,
        double quantity
) {
}
