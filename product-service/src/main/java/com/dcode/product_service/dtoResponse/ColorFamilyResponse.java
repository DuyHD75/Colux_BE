package com.dcode.product_service.dtoResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColorFamilyResponse {
    private String colorFamilyId;
    private String name;
    private String description;
}
