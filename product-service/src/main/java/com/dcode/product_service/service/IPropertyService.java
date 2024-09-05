package com.dcode.product_service.service;

import com.dcode.product_service.dtoResponse.PropertyResponse;

import java.util.Set;

public interface IPropertyService {
    void createAProperty(String name, String description, Set<String> propertyValues);

    PropertyResponse getAProperty(String propertyId);
}
