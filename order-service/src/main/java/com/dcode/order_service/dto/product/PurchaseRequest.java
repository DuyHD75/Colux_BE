package com.dcode.order_service.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        String productId,
        String paintId,
        String wallpaperId,
        String floorId,
        @NotNull(message = "Variant is mandatory")
        String variantId,
        @Positive(message = "Quantity must be greater than zero")
        double quantity
) {
}
