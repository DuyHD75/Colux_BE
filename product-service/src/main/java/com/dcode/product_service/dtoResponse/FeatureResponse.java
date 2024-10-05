package com.dcode.product_service.dtoResponse;

import com.dcode.product_service.entity.FeatureValue;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class FeatureResponse {
    private String featureId;
    private String name;
    private String description;
    private Set<FeatureValue> featureValues;
}

