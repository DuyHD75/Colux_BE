package com.dcode.product_service.dtoRequest;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class WallpaperRequest {
    private String productId;
    private String area;
    private Set<VariantRequest> variants;
}
