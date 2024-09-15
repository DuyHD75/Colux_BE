package com.dcode.product_service.dtoRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColorFamilyRequest {
    private String name;
    private String description;
}
