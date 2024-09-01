package com.dcode.product_service.dtoResponse;


import com.dcode.product_service.entity.Category;
import com.dcode.product_service.entity.Product;
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
    private String quantity;
    private String color;
    private Set<VariantResponse> variants;
}
