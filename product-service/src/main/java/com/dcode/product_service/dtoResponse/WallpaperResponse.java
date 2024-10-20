package com.dcode.product_service.dtoResponse;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WallpaperResponse {
    @JsonProperty("id")
    private String wallpaperId;
    private String area;
    private List<VariantResponse> variants;
    @JsonBackReference
    private ProductResponse product;
}
