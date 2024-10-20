package com.dcode.product_service.dtoRequest;

import lombok.Data;

import java.util.Set;

@Data
public class WallpaperRequest {
    private String productId;
    private String area;
    private Set<VariantRequest> variants;
}
