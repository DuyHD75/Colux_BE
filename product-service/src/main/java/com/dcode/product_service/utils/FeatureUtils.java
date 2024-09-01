package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.FeatureResponse;
import com.dcode.product_service.entity.Feature;
import com.dcode.product_service.entity.FeatureValue;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureUtils {
    public static Feature createNewFeatureEntity(String name, String description, Set<String> featureValue){
        Feature feature = Feature.builder()
                .featureId(UUID.randomUUID().toString())
                .name(name)
                .description(description)
                .build();
        Set<FeatureValue> featureValueSet = mapToListFeatureValue(featureValue, feature);
        feature.setFeatureValues(featureValueSet);
        return feature;
    }
    public static Feature updateFeatureEntity(String name, String description, Set<String> featureValues, Feature feature){
            feature.setName(name);
            feature.setDescription(description);
            Set<String> newFeatureValues = new HashSet<>();
            for (String featureValue: featureValues){
                    if (!feature.getFeatureValues().contains(featureValue)){
                        newFeatureValues.add(featureValue);
                    }
            }
            feature.getFeatureValues().clear();
            feature.getFeatureValues().addAll(mapToListFeatureValue(newFeatureValues, feature));
        return feature;
    }
    private static Set<FeatureValue> mapToListFeatureValue(Set<String> features, Feature feature){
        if (features == null) return null;
        return features.stream()
                .map(featureValue -> {
                    FeatureValue fv = FeatureValue.builder().value(featureValue).build();
                    fv.setFeature(feature);
                    return fv;
                })
                    .collect(Collectors.toSet());
    }
    public static FeatureResponse fromFeatureEntity(Feature feature){
        FeatureResponse featureResponse = new FeatureResponse();
        BeanUtils.copyProperties(feature, featureResponse);
        return featureResponse;
    }
}
