package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.PropertyResponse;
import com.dcode.product_service.entity.Property;
import com.dcode.product_service.entity.PropertyValue;
import org.springframework.beans.BeanUtils;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PropertyUtils {
    public static Property createNewPropertyEntity(String name, String description, Set<String> propertyValueSet) {
        Property property = Property.builder()
                .propertyId(UUID.randomUUID().toString())
                .name(name)
                .description(description)
                .build();
        Set<PropertyValue> propertyValues = mapToListPropertyValue(propertyValueSet, property);
        property.setPropertyValues(propertyValues);
        return property;
    }

    private static Set<PropertyValue> mapToListPropertyValue(Set<String> propertyValueSet, Property property) {
        if (propertyValueSet == null) return null;
        return propertyValueSet.stream()
                .map(propertyValue -> {
                    PropertyValue pv = PropertyValue.builder().value(propertyValue).build();
                    pv.setProperty(property);
                    return pv;
                }).collect(Collectors.toSet());
    }
    public static PropertyResponse fromPropertyEntity(Property property){
        PropertyResponse propertyResponse = new PropertyResponse();
        BeanUtils.copyProperties(property, propertyResponse);

        propertyResponse.setPropertyValues(property.getPropertyValues().stream()
                .map(PropertyValue::getValue)
                .collect(Collectors.toSet()));
        return propertyResponse;
    }
}
