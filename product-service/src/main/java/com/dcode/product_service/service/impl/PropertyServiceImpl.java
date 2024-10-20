package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.RequestProperty;
import com.dcode.product_service.dtoResponse.PropertyResponse;
import com.dcode.product_service.entity.Property;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.PropertyRepository;
import com.dcode.product_service.service.IPropertyService;
import com.dcode.product_service.utils.PropertyUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.PropertyUtils.createNewPropertyEntity;
import static com.dcode.product_service.utils.PropertyUtils.fromPropertyEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
@AllArgsConstructor
public class PropertyServiceImpl implements IPropertyService {

    private final PropertyRepository propertyRepository;

    @Override
    public void createProperties(Set<RequestProperty> requestProperties) {
       propertyRepository.saveAll(createNewProperties(requestProperties));
    }

    @Override
    public PropertyResponse getAProperty(String propertyId) {
      var property =  propertyRepository.findByPropertyId(propertyId).orElseThrow(() -> new ApiException("Property not found!"));
        return fromPropertyEntity(property);
    }

    @Override
    public List<PropertyResponse> getAllProperty() {
        var properties = propertyRepository.findAll();
        return properties.stream().map(PropertyUtils::fromPropertyEntity).collect(Collectors.toList());
    }

    private Set<Property> createNewProperties(Set<RequestProperty> requestProperties) {
        return requestProperties.stream().map(
                PropertyUtils::createNewPropertyEntity
        ).collect(Collectors.toSet());
    }
}

