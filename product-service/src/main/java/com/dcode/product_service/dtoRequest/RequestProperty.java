package com.dcode.product_service.dtoRequest;

import com.dcode.product_service.entity.PropertyValue;
import lombok.Data;

import java.util.Set;

@Data
public class RequestProperty {
    private String name;
    private String description;
    private Set<String> propertyValues;
}
