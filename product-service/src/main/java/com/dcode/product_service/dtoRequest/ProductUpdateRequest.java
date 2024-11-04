package com.dcode.product_service.dtoRequest;

import com.dcode.product_service.dtoResponse.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class ProductUpdateRequest {
    private String productId;
    private String description;
    private String placeOfOrigin;
    private String productName;
    private Double ratingAverage;
    private String code;
    private String warranty;
    private String applicableSurface;
    private CategoryResponse category;
    private BrandResponse brand;
    private Set<ImageResponse> images;
    private Set<FeatureValueResponse> features;
    private Set<PropertyValueResponse> properties;


    private Set<PaintResponse> paints;
    private Set<WallpaperResponse> wallpapers;
    private Set<FloorResponse> floors;

}
