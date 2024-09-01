package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoResponse.FeatureResponse;
import com.dcode.product_service.entity.Feature;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.FeatureRepository;
import com.dcode.product_service.service.IFeatureService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.dcode.product_service.utils.FeatureUtils.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class FeatureServiceImpl implements IFeatureService {

    private final FeatureRepository featureRepository;

    @Override
    public void createFeature(String name, String description, Set<String> featureValue) {
        featureRepository.save(createNewFeature(name, description, featureValue));
    }

    @Override
    public void updateFeature(String name, String description, Set<String> featureValue, String featureId) {
        var feature = featureRepository.findByFeatureId(featureId).orElseThrow(() -> new ApiException("Error: Feature is not found."));
        log.info(String.format("Updating feature: %s", name));
        featureRepository.save(updateFeatureEntity(name, description, featureValue, feature));

    }

    @Override
    public FeatureResponse getFeature(String featureId) {
        var featureEntity = featureRepository.findByFeatureId(featureId).orElseThrow(() -> new ApiException("Feature not found!"));
        return fromFeatureEntity(featureEntity);
    }

    private Feature createNewFeature(String name, String description, Set<String> featureValue) {
        log.info(String.format("Creating new feature: %s", name));
        return createNewFeatureEntity(name, description, featureValue);
    }
}
