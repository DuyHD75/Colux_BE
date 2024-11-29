package com.dcode.product_service.dtoRequest;


import com.dcode.product_service.entity.Category;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PaintRequest {
    private String productId;
    private String color;
    private Set<VariantRequest> variants;
}
