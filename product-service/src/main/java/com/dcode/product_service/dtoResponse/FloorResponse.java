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
    private String foamThickness;
    private String numberOfPiecesPerBox;
    private String areaPerBox;
    private List<VariantResponse> variants;
    @JsonBackReference
    private ProductResponse product;
}
