package com.dcode.product_service.dtoResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelativeCollectionResponse {
    @JsonProperty("id")
    private String relativeCollectionId;
    private String name;
}
