package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByFeatureId (String featureId);
}
