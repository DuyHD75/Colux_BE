package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.ColorRequest;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.Color;

public interface IColorService {
    void createAColor(String name, String code, String description);

    ColorResponse getAColor(String colorId);

    void updateAColor(String colorId, ColorRequest colorRequest);

    void deleteAColor(String colorId);
}
