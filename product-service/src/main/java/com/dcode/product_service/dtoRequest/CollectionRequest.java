package com.dcode.product_service.dtoRequest;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CollectionRequest {
    private String name;
    private Set<String> colors;
    private String colorFamilyId;
    private String roomId;
    private String collectionTypeId;
    private String relativeCollectionId;

}
