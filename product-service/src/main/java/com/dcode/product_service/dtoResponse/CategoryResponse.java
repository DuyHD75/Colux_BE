package com.dcode.product_service.dtoResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private String categoryId;
    private String name;
    private String thumbnail;
}
