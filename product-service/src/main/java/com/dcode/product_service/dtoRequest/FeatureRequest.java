package com.dcode.product_service.dtoRequest;

import lombok.Data;

import java.util.Set;

@Data
public class FeatureRequest {
    private String name;
    private String description;
    private Set<String> featureValue;
}
