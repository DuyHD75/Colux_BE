package com.dcode.product_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDTO {
    private String categoryId;
    private String name;
    private String thumbnail;
}
