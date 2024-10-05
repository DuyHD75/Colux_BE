package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.FeatureValueResponse;
import com.dcode.product_service.entity.FeatureValue;

import java.util.Set;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.FeatureUtils.fromFeatureEntity;
import static com.dcode.product_service.utils.PropertyUtils.fromPropertyEntity;

public class FeatureValueUtils {
    public static Set<FeatureValueResponse> fromFeatureValueEntity(Set<FeatureValue> featureValues){
        Set<FeatureValueResponse> featureValueSet = featureValues.stream()
                .map(featureValue -> {
                    FeatureValueResponse response =  FeatureValueResponse.builder()
                            .featureValueId(featureValue.getFeatureValueId())
                            .value(featureValue.getValue())
                            .feature(fromFeatureEntity(featureValue.getFeature()))
                            .build();
                    return response;
                }).collect(Collectors.toSet());
        return featureValueSet;

    }
}
