package com.dcode.product_service.utils;

import com.dcode.product_service.dto.ProductBrandDTO;
import com.dcode.product_service.dto.ProductCategoryDTO;
import com.dcode.product_service.dtoRequest.ProductRequest;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.*;

import java.util.Set;
import java.util.UUID;

import static com.dcode.product_service.utils.FeatureValueUtils.fromFeatureValueEntity;
import static com.dcode.product_service.utils.PaintUtils.fromPaintEntity;
import static com.dcode.product_service.utils.PropertyValueUtils.fromPropertyValueEntity;

public class ProductUtils {

    public static Product createNewProductEntity(ProductRequest productRequest,
                                                 Brand brand, Category category, Set<FeatureValue> features, Set<PropertyValue> properties) {
        return Product.builder()
                .productId(UUID.randomUUID().toString())
                .productName(productRequest.getProductName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .ratingAverage(productRequest.getRatingAverage())
                .code(productRequest.getCode())
                .placeOfOrigin(productRequest.getPlaceOfOrigin())
                .warranty(productRequest.getWarranty())
                .applicableSurface(productRequest.getApplicableSurface())
                .brand(brand)
                .category(category)
                .featureValues(features)
                .propertyValues(properties)
                .build();
    }

    public static ProductResponse fromProductEntity(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .ratingAverage(product.getRatingAverage())
                .code(product.getCode())
                .placeOfOrigin(product.getPlaceOfOrigin())
                .warranty(product.getWarranty())
                .applicableSurface(product.getApplicableSurface())
                .images(product.getImages().stream().map(ImageUtils::fromImageEntity).toList())
                .category(mapToProductCategoryDTO(product.getCategory()))
                .features(fromFeatureValueEntity(product.getFeatureValues()))
                .properties(fromPropertyValueEntity(product.getPropertyValues()))
                .paints(product.getPaints().stream().map(PaintUtils::fromPaintEntity).toList())
                .wallpapers(product.getWallpapers().stream().map(WallpaperUtils::fromWallpaperEntity).toList())
                .floors(product.getFloors().stream().map(FloorUtils::fromFloorEntity).toList())
                .build();
    }
    public static ProductResponse fromProductEntitySimple(Product product){
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .ratingAverage(product.getRatingAverage())
                .code(product.getCode())
                .placeOfOrigin(product.getPlaceOfOrigin())
                .warranty(product.getWarranty())
                .applicableSurface(product.getApplicableSurface())
                .images(product.getImages().stream().map(ImageUtils::fromImageEntity).toList())
                .category(mapToProductCategoryDTO(product.getCategory()))
                .features(fromFeatureValueEntity(product.getFeatureValues()))
                .properties(fromPropertyValueEntity(product.getPropertyValues()))
                .build();
    }

    private static ProductCategoryDTO mapToProductCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }
        return ProductCategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .thumbnail(category.getThumbnail())
                .build();
    }

    private static ProductBrandDTO mapToProductBrandDTO(Brand brand) {
        if (brand == null) {
            return null;
        }
        return ProductBrandDTO.builder()
                .brandId(brand.getBrandId())
                .name(brand.getName())
                .code(brand.getCode())
                .status(brand.getStatus())
                .build();
    }
}
