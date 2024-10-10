package com.dcode.product_service.dtoRequest;


import com.dcode.product_service.entity.Category;
import lombok.Data;

import java.util.Set;

@Data
public class PaintRequest {
    private String color;
    private Set<VariantRequest> variants;
}
