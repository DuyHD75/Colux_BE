package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.FeatureRequest;
import com.dcode.product_service.dtoResponse.FeatureResponse;
import com.dcode.product_service.dtoResponse.FeatureValueResponse;
import com.dcode.product_service.dtoResponse.PropertyValueResponse;
import com.dcode.product_service.entity.Feature;
import com.dcode.product_service.entity.FeatureValue;
import com.dcode.product_service.entity.PropertyValue;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.FeatureValueUtils.fromFeatureValueEntity;
import static com.dcode.product_service.utils.PropertyUtils.fromPropertyEntity;

public class FeatureUtils {
    public static Feature createNewFeatureEntity(FeatureRequest featureRequest) {
        Feature feature = Feature.builder()
                .featureId(UUID.randomUUID().toString())
                .category(featureRequest.getCategory())
                .name(featureRequest.getName())
                .description(featureRequest.getDescription())
                .build();
        Set<FeatureValue> featureValueSet = mapToListFeatureValue(featureRequest.getFeatureValue(), feature);
        feature.setFeatureValues(featureValueSet);
        return feature;
    }
    public static Feature updateFeatureEntity(FeatureRequest featureRequest, Feature feature){
            feature.setName(featureRequest.getName());
            feature.setDescription(featureRequest.getDescription());
            feature.setCategory(feature.getCategory());
            feature.setCategory(featureRequest.getCategory());
            Set<String> newFeatureValues = new HashSet<>();
            for (String featureValue: featureRequest.getFeatureValue()){
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
                    FeatureValue fv = FeatureValue.builder().featureValueId(UUID.randomUUID().toString()).value(featureValue).build();
                    fv.setFeature(feature);
                    return fv;
                })
                    .collect(Collectors.toSet());
    }
    public static FeatureResponse fromFeatureEntity(Feature feature){
        return FeatureResponse.builder()
                .featureId(feature.getFeatureId())
                .name(feature.getName())
                .description(feature.getDescription())
                .category(feature.getCategory())
                .featureValues(fromFeatureValueEntity(feature.getFeatureValues()))
                .build();
    }


    public static Set<FeatureValueResponse> fromFeatureValueEntities(Set<FeatureValue> featureValues, boolean includeFeature) {
        return featureValues.stream()
                .map(featureValue -> FeatureValueResponse.builder()
                        .featureValueId(featureValue.getFeatureValueId())
                        .value(featureValue.getValue())
                        .feature(includeFeature ? fromFeatureEntityWithoutValues(featureValue.getFeature()) : null)
                        .build())
                .collect(Collectors.toSet());
    }

    public static FeatureResponse fromFeatureEntityWithoutValues(Feature feature) {
        return FeatureResponse.builder()
                .featureId(feature.getFeatureId())
                .name(feature.getName())
                .description(feature.getDescription())
                .build();
    }

}
