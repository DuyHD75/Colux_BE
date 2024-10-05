package com.dcode.product_service.dtoResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColorFamilyResponse {
    @JsonProperty("id")
    private String colorFamilyId;
    private String name;
    private String title;
    private String description;
    private String hex;
    private String image;
    private Set<CollectionResponse> collections;
}
