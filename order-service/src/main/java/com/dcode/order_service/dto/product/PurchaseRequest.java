package com.dcode.order_service.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        @NotNull(message = "Product is mandatory")
        String productId,
        @Positive(message = "Quantity must be greater than zero")
        Integer quantity
) {
}
