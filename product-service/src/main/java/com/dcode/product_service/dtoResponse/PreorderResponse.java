package com.dcode.product_service.dtoResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreorderResponse {
    @JsonProperty("id")
    private String preorderId;
    private Double quantity;
    private VariantResponse variant;
    private ProductResponse product;

    // fields for identify paint, floor or wallpaper.
    private String identity;
}
