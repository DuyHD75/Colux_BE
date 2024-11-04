package com.dcode.product_service.dtoResponse;

import com.dcode.product_service.entity.PropertyValue;
import lombok.Data;

import java.util.Set;

@Data
public class PropertyResponse {
    private String propertyId;
    private String name;
    private String description;
    private Set<PropertyValueResponse> propertyValues;
}
