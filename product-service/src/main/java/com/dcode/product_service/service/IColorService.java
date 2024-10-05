package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.ColorRequest;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.Color;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IColorService {
    void createAColor(ColorRequest colorRequest);

    ColorResponse getAColor(String colorId);

    void updateAColor(String colorId, ColorRequest colorRequest);

    void deleteAColor(String colorId);

    List<ColorResponse> getAllColor();

    PageResponse<ColorResponse> getColorByColorFamilyAndCollection(String collectionId, String colorFamilyId, Pageable pageable);
}
