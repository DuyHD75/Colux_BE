package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.RequestProperty;
import com.dcode.product_service.dtoResponse.PropertyResponse;

import java.util.List;
import java.util.Set;

public interface IPropertyService {
    void createProperties(Set<RequestProperty> requestProperties);

    PropertyResponse getAProperty(String propertyId);

    List<PropertyResponse> getAllProperty();
}
