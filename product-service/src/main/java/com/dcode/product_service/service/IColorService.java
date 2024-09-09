package com.dcode.product_service.service;

import com.dcode.product_service.entity.Color;

public interface IColorService {
    void createAColor(String name, String code, String description);

    Color getAColor(String colorId);
}
