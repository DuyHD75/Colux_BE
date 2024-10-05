package com.dcode.product_service.service;

import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.dtoResponse.ColorFamilyResponse;
import com.dcode.product_service.entity.Color;

import java.util.List;
import java.util.Set;

public interface IColorFamilyService {
    void createACF(String name, String title, String description, String hex, String image);

    ColorFamilyResponse getAColorFamily(String colorFamilyId);

    List<ColorFamilyResponse> getAllColorFamily();

    List<CollectionResponse> getColorByColorFamily(String colorFamilyId);
}
