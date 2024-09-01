package com.dcode.product_service.dtoRequest;


import com.dcode.product_service.entity.Category;
import lombok.Data;

import java.util.Set;

@Data
public class PaintRequest {
    private String quantity;
    private String color;
    private Set<String> variants;
}
