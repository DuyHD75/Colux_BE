package com.dcode.product_service.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class WallpaperResponse {
    private String area;
    private Set<VariantResponse> variants;
}
