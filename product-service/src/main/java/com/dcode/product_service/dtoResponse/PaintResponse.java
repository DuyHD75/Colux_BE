package com.dcode.product_service.dtoResponse;


import com.dcode.product_service.entity.Category;
import com.dcode.product_service.entity.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaintResponse {
    @JsonProperty("id")
    private String paintId;
    private String color;
    private List<VariantResponse> variants;
    private ProductResponse product;
}
