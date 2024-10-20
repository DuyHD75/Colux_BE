package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.FeatureRequest;
import com.dcode.product_service.dtoResponse.FeatureResponse;
import com.dcode.product_service.entity.Feature;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.FeatureRepository;
import com.dcode.product_service.service.IFeatureService;
import com.dcode.product_service.utils.FeatureUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.FeatureUtils.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class FeatureServiceImpl implements IFeatureService {

    private final FeatureRepository featureRepository;

    @Override
    public void createFeatures(Set<FeatureRequest> featureRequest) {
        featureRepository.saveAll(createNewFeature(featureRequest));
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

    @Override
    public List<FeatureResponse> getAllFeature() {
        var featureEntityList = featureRepository.findAll();
        return featureEntityList.stream().map(FeatureUtils::fromFeatureEntity).collect(Collectors.toList());
    }

    private Set<Feature> createNewFeature(Set<FeatureRequest> featureRequests) {
        return featureRequests.stream().map(
                FeatureUtils::createNewFeatureEntity)
                .collect(Collectors.toSet());
    }
}
