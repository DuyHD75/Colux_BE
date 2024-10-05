package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByFeatureId (String featureId);
    Set<Feature> findByFeatureIdIn(Set<String> ids);
}
