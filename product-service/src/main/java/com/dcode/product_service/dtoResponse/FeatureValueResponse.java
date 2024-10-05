package com.dcode.product_service.dtoResponse;

import com.dcode.product_service.entity.Feature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class FeatureValueResponse {
    @JsonProperty("id")
    private String featureValueId;
    private String value;
    private FeatureResponse feature;
}
