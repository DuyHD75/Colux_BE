package com.dcode.order_service.dto.order.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderVariantRequest {
    private Long variantId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
}
