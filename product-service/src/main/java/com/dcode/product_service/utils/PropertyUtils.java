package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.RequestProperty;
import com.dcode.product_service.dtoResponse.PropertyResponse;
import com.dcode.product_service.entity.Property;
import com.dcode.product_service.entity.PropertyValue;
import org.springframework.beans.BeanUtils;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.PropertyValueUtils.fromPropertyValueEntity;

public class PropertyUtils {
    public static Property createNewPropertyEntity(RequestProperty requestProperty) {
        Property property = Property.builder()
                .propertyId(UUID.randomUUID().toString())
                .name(requestProperty.getName())
                .description(requestProperty.getDescription())
                .build();
        Set<PropertyValue> propertyValues = mapToListPropertyValue(requestProperty.getPropertyValues(), property);
        property.setPropertyValues(propertyValues);
        return property;
    }

    private static Set<PropertyValue> mapToListPropertyValue(Set<String> propertyValueSet, Property property) {
        if (propertyValueSet == null) return null;
        return propertyValueSet.stream()
                .map(propertyValue -> {
                    PropertyValue pv = PropertyValue.builder().propertyValueId(UUID.randomUUID().toString()).value(propertyValue).build();
                    pv.setProperty(property);
                    return pv;
                }).collect(Collectors.toSet());
    }
    public static PropertyResponse fromPropertyEntity(Property property){
        PropertyResponse propertyResponse = new PropertyResponse();
        BeanUtils.copyProperties(property, propertyResponse);

        propertyResponse.setPropertyValues(fromPropertyValueEntity(property.getPropertyValues()));
        // This is the original code
//        propertyResponse.setPropertyValues(property.getPropertyValues().stream()
//                .map(PropertyValue::getValue)
//                .collect(Collectors.toSet()));
        return propertyResponse;
    }
}
