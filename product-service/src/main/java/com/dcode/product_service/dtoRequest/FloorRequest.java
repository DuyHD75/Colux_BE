package com.dcode.product_service.dtoRequest;

import lombok.Data;

import java.util.Set;

@Data
public class FloorRequest {
    private Double foamThickness;
    private String accessoryType;

    private String packagingMaterial;
    private Integer numberOfPiecesPerBox;
//    private Double areaPerBox; => sizeName in variant class

    private Set<VariantRequest> variants;
}
