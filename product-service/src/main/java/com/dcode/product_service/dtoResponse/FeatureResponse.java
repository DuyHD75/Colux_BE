package com.dcode.product_service.dtoResponse;

import com.dcode.product_service.entity.FeatureValue;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class FeatureResponse {
    private String featureId;
    private String name;
    private String description;
    private Set<FeatureValueResponse> featureValues;
}

