package com.dcode.product_service.repository;

import com.dcode.product_service.entity.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PropertyValueRepository extends JpaRepository<PropertyValue, Long> {
    Set<PropertyValue> findByPropertyValueIdIn(Set<String> ids);
}
