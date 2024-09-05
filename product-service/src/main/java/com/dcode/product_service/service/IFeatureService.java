package com.dcode.product_service.service;

import com.dcode.product_service.dtoResponse.FeatureResponse;

import java.util.List;
import java.util.Set;

public interface IFeatureService {
    void createFeature(String name, String description, Set<String> featureValue);

    void updateFeature(String name, String description, Set<String> featureValue, String featureId);

    FeatureResponse getFeature(String featureId);
}
