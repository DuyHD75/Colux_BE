package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.ColorRequest;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IColorService {
    void createColors(List<ColorRequest> colorRequest);

    ColorResponse getAColor(String colorId);

    void updateAColor(String colorId, ColorRequest colorRequest);

    void deleteAColor(String colorId);

    PageResponse<ColorResponse> getAllColor(Pageable pageable);

    PageResponse<ColorResponse> getColorByColorFamilyAndCollection(String collectionId, String colorFamilyId, Pageable pageable);

    PageResponse<ColorResponse> getColor(Boolean interior, Boolean exterior, Pageable pageable);

    PageResponse<ColorResponse> getColorByCollectionAndRoom(String collectionId, String roomId, Pageable pageable);
}
