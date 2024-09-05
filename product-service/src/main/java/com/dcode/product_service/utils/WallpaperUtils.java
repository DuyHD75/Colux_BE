package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.VariantResponse;
import com.dcode.product_service.dtoResponse.WallpaperResponse;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.entity.Variant;
import com.dcode.product_service.entity.Wallpaper;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class WallpaperUtils {
    public static Wallpaper createNewWallpaperEntity(Product product, String area, Set<VariantRequest> variantRequestSet, Set<Variant> currentVariants){
        Wallpaper wallpaper = Wallpaper.builder()
                .wallpaperId(UUID.randomUUID().toString())
                .area(Double.parseDouble(area))
                .product(product)
                .build();
        Set<Variant> variants = variantRequestSet.stream().map(
                variantRequest -> {
                    // check Variant exists
                    return currentVariants.stream()
                            .filter(v -> v.getSizeName().equals(variantRequest.getSizeName())
//                                    && v.getCategoryName().equals(product.getCategory().toString())
                                    && v.getPackageType().equals(variantRequest.getPackageType()))
                            .findFirst()
                            .orElseGet(() -> {
                                // if not exist, create new one. new variant saved to bc because we're using cascade type = all
                                Variant newVariant = Variant.builder()
                                        .variantId(UUID.randomUUID().toString())
                                        .sizeName(variantRequest.getSizeName())
                                        .categoryName(product.getCategory().toString())
                                        .packageType(variantRequest.getPackageType())
                                        .build();
//                                newVariant.setWallpaper(wallpaper);
                                return newVariant;
                            });
                }).collect(Collectors.toSet());
        wallpaper.setVariants(variants);
        return wallpaper;
    }
    public static WallpaperResponse fromWallpaperEntity(Wallpaper wallpaper){
        Set<VariantResponse> variantResponses = new HashSet<>();
        if (wallpaper.getVariants() != null){
            wallpaper.getVariants().forEach(variant -> {
                VariantResponse response = new VariantResponse();
                BeanUtils.copyProperties(variant, response);
                variantResponses.add(response);
            });
        }
        return WallpaperResponse.builder()
                .area(String.valueOf(wallpaper.getArea()))
                .variants(variantResponses)
                .build();
    }

}
