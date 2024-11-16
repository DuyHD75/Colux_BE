package com.dcode.product_service.dtoResponse;

import com.dcode.product_service.entity.PropertyValue;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PropertyResponse {
    private String propertyId;
    private String name;
    private String description;
    private String category;
    private Set<PropertyValueResponse> propertyValues;
}
