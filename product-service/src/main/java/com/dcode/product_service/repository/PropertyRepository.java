package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    Optional<Property> findByPropertyId (String propertyId);
    Set<Property> findByPropertyIdIn(Set<String> ids);
}
