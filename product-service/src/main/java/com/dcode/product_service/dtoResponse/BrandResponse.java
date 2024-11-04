package com.dcode.product_service.dtoResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandResponse {
    private String brandId;
    private String name;
    private String code;
    private String status;
}
