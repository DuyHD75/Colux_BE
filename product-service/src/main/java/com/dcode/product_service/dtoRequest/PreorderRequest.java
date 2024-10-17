package com.dcode.product_service.dtoRequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreorderRequest {
    private Double quantity;
    private String variantId;
    private String productId;
    private Integer status;
    // fields for identify paint, floor or wallpaper.
    private String identity;

}
