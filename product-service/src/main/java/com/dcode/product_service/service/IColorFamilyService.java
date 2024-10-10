package com.dcode.product_service.service;

import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.dtoResponse.ColorFamilyResponse;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.Color;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface IColorFamilyService {
    void createACF(String name, String title, String description, String hex, String image);

    ColorFamilyResponse getAColorFamily(String colorFamilyId);

    List<ColorFamilyResponse> getAllColorFamily();

    PageResponse<ColorResponse> getColorByColorFamily(String colorFamilyId, Pageable pageable);
}
