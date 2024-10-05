package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.CollectionRequest;
import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface ICollectionService {
    void createACollection(CollectionRequest collectionRequest);

    CollectionResponse getACollection(String collectionId);

    List<CollectionResponse> getAllCollection();

    PageResponse<ColorResponse> getColorByCollection(String collectionId, Pageable pageable);

    List<CollectionResponse> getAllCollectionWithoutColorFamilyAndRoom();
}
