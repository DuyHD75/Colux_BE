package com.dcode.product_service.dtoRequest;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CollectionRequest {
    private String collectionId;
    private String name;
    private String title;
    private String description;
    private String image;
    private String hex;
    private Set<String> colors;
    private String colorFamilyId;
    private String roomId;
    private String collectionTypeId;
    private String relativeCollectionId;

}
