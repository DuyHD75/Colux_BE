package com.dcode.order_service.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseResponse {
    private String productId;
    private String paintId;
    private String wallpaperId;
    private String floorId;
    private String variantId;
    private Double quantity;
    private Double price;
    private String message;
    private Boolean success;
}
