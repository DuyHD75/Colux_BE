package com.dcode.product_service.dtoResponse;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FloorResponse {
    @JsonProperty("id")
    private String floorId;
    private Double foamThickness;
    private Integer numberOfPiecesPerBox;
    private String areaPerBox;
    private Integer status;
    private List<VariantResponse> variants;
    @JsonBackReference
    private ProductResponse product;
}
