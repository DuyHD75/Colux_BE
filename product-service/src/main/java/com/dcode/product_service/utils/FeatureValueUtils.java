package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.FeatureValueResponse;
import com.dcode.product_service.entity.FeatureValue;

import java.util.Set;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.FeatureUtils.fromFeatureEntity;
import static com.dcode.product_service.utils.PropertyUtils.fromPropertyEntity;

public class FeatureValueUtils {
    public static Set<FeatureValueResponse> fromFeatureValueEntity(Set<FeatureValue> featureValues) {
        return featureValues.stream()
                .map(featureValue -> FeatureValueResponse.builder()
                        .featureValueId(featureValue.getFeatureValueId())
                        .value(featureValue.getValue())
                        .feature(FeatureUtils.fromFeatureEntityWithoutValues(featureValue.getFeature()))
                        .build())
                .collect(Collectors.toSet());
    }

    public static FeatureValueResponse fromFeatureValueEntity(FeatureValue featureValue) {
        return FeatureValueResponse.builder()
                .featureValueId(featureValue.getFeatureValueId())
                .value(featureValue.getValue())
                .feature(FeatureUtils.fromFeatureEntityWithoutValues(featureValue.getFeature()))
                .build();
    }
}
