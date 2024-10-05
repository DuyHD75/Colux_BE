package com.dcode.product_service.dtoRequest;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ColorFamilyRequest {
    private String name;
    private String title;
    private String description;
    private String hex;
    private String image;
    private Set<String> collections;
}
