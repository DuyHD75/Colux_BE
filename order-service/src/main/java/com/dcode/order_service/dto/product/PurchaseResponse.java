package com.dcode.order_service.dto.product;

import java.math.BigDecimal;

public record PurchaseResponse(
        String productId,
        String name,
        BigDecimal listPrice,
        BigDecimal payPrice,
        double quantity
) {
}
