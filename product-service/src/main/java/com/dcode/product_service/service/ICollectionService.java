package com.dcode.product_service.service;

import com.dcode.product_service.dtoResponse.CollectionResponse;

import java.util.Set;

public interface ICollectionService {
    void createACollection(String name, Set<String> colors, String colorFamilyId, String roomId, String collectionTypeId, String relativeCollectionId);

    CollectionResponse getACollection(String collectionId);
}
