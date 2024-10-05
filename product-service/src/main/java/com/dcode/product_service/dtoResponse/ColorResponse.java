package com.dcode.product_service.dtoResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ColorResponse {
    @JsonProperty("id")
    private String colorId;
    private String name;
    private String image;
    private String code;
    private String hex;
    private String LVR;
    private boolean interior;
    private boolean exterior;
    private String description;
    private long colorTypeId;
    private Set<CollectionResponse> collections;
    private Set<ColorFamilyResponse> colorFamily;
}
