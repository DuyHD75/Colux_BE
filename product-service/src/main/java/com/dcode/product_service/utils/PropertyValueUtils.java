package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.PropertyResponse;
import com.dcode.product_service.dtoResponse.PropertyValueResponse;
import com.dcode.product_service.entity.PropertyValue;

import java.util.Set;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.PropertyUtils.buildPropertyResponse;
import static com.dcode.product_service.utils.PropertyUtils.fromPropertyEntity;

public class PropertyValueUtils {
    public static Set<PropertyValueResponse> fromPropertyValueEntity(Set<PropertyValue> propertyValues){

        return propertyValues.stream()
                        .map(propertyValue -> {
                            PropertyResponse propertyResponse = buildPropertyResponse(propertyValue.getProperty());
                            propertyResponse.setPropertyValues(null);
                            return PropertyValueResponse.builder()
                                    .propertyValueId(propertyValue.getPropertyValueId())
                                    .value(propertyValue.getValue())
                                    .property(propertyResponse)
                                    .build();
                        }).collect(Collectors.toSet());

    }
}
