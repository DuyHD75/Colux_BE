package com.dcode.product_service.dtoResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyValueResponse {
    @JsonProperty("id")
    private String propertyValueId;
    private String value;
    private PropertyResponse property;
}
