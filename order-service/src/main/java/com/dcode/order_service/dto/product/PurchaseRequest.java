package com.dcode.order_service.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        @NotNull(message = "Product is mandatory")
        String productId,
        @NotNull(message = "Variant is mandatory")
        String variantId,
        String colorId,
        @Positive(message = "Quantity must be greater than zero")
        double quantity
) {
}
