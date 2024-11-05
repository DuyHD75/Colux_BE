package com.dcode.product_service.repository;

import com.dcode.product_service.entity.FeatureValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface FeatureValueRepository extends JpaRepository<FeatureValue, Long> {
    Set<FeatureValue> findByFeatureValueIdIn(Set<String> ids);
    Optional<FeatureValue> findByFeatureValueId(String featureValueId);
}
