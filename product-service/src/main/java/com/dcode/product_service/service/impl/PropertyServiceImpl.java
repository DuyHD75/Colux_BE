package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoResponse.PropertyResponse;
import com.dcode.product_service.entity.Property;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.PropertyRepository;
import com.dcode.product_service.service.IPropertyService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.dcode.product_service.utils.PropertyUtils.createNewPropertyEntity;
import static com.dcode.product_service.utils.PropertyUtils.fromPropertyEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
@AllArgsConstructor
public class PropertyServiceImpl implements IPropertyService {

    private final PropertyRepository propertyRepository;

    @Override
    public void createAProperty(String name, String description, Set<String> propertyValues) {
       propertyRepository.save(createNewProperty(name,description, propertyValues));
    }

    @Override
    public PropertyResponse getAProperty(String propertyId) {
      var property =  propertyRepository.findByPropertyId(propertyId).orElseThrow(() -> new ApiException("Property not found!"));
        return fromPropertyEntity(property);
    }

    private Property createNewProperty(String name, String description, Set<String> propertyValues) {
        return createNewPropertyEntity(name, description, propertyValues);
    }
}
