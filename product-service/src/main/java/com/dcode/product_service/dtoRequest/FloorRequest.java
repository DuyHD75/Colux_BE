package com.dcode.product_service.dtoRequest;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class FloorRequest {
    private String productId;
    private Double foamThickness;
    private Integer numberOfPiecesPerBox;
    private Integer status;
//    private Double areaPerBox; => sizeName in variant class

    private Set<VariantRequest> variants;
}
