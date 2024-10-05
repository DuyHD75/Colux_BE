package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.PropertyValueResponse;
import com.dcode.product_service.entity.PropertyValue;

import java.util.Set;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.PropertyUtils.fromPropertyEntity;

public class PropertyValueUtils {
    public static Set<PropertyValueResponse> fromPropertyValueEntity(Set<PropertyValue> propertyValues){
        Set<PropertyValueResponse> propertyValueSet = propertyValues.stream()
                        .map(propertyValue -> {
                            PropertyValueResponse response =  PropertyValueResponse.builder()
                                    .propertyValueId(propertyValue.getPropertyValueId())
                                    .value(propertyValue.getValue())
                                    .property(fromPropertyEntity(propertyValue.getProperty()))
                                    .build();
                            return response;
                        }).collect(Collectors.toSet());
        return propertyValueSet;

    }
}
