package com.dcode.product_service.dtoRequest;

import lombok.Data;

import java.util.Set;

@Data
public class FloorRequest {
    private String productId;
    private Double foamThickness;
    private Integer numberOfPiecesPerBox;
//    private Double areaPerBox; => sizeName in variant class

    private Set<VariantRequest> variants;
}
