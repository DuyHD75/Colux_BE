package com.dcode.product_service.dtoResponse;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FloorResponse {
    private String foamThickness;
    private String accessoryType;

    private String packagingMaterial;
    private String numberOfPiecesPerBox;
    private String areaPerBox;
    private List<VariantResponse> variants;
}
