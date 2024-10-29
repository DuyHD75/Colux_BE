package com.dcode.order_service.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseResponse {
    private String productId;
    private String paintId;
    private String wallpaperId;
    private String floorId;
    private String variantId;
    private Integer quantity;
    private BigDecimal price;
    private String message;
    private Boolean success;
}
