package com.dcode.product_service.dtoResponse;

import com.dcode.product_service.dto.ProductBrandDTO;
import com.dcode.product_service.dto.ProductCategoryDTO;
import com.dcode.product_service.entity.Brand;
import com.dcode.product_service.entity.Category;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private String productId;
    private String createdAt;
    private String createdBy;
    private String updateAt;
    private String updatedBy;
    private String description;
    private String placeOfOrigin;
    private String price;
    private String productName;
    private String ratingAverage;
    private String code;
    private String warranty;
    private String applicableSurface;
    private ProductCategoryDTO category;
    private ProductBrandDTO brand;
    private List<ImageResponse> images;
    private Set<FeatureValueResponse> features;
    private Set<PropertyValueResponse> properties;


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PaintResponse> paints;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<WallpaperResponse> wallpapers;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FloorResponse> floors;

}
