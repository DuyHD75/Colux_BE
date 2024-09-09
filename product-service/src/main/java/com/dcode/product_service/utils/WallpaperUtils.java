package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.VariantResponse;
import com.dcode.product_service.dtoResponse.WallpaperResponse;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.entity.Variant;
import com.dcode.product_service.entity.Wallpaper;
import com.dcode.product_service.entity.WallpaperVariant;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.PaintUtils.convertVariantToVResponse;

public class WallpaperUtils {
    public static Wallpaper createNewWallpaperEntity(Product product, String area, Map<Variant, Double> variantRequestSet){
        Set< WallpaperVariant> wallpaperVariants = new HashSet<>();
        Wallpaper wallpaper = Wallpaper.builder()
                .wallpaperId(UUID.randomUUID().toString())
                .product(product)
                .area(Double.parseDouble(area))
                .wallpaperVariants(wallpaperVariants)
                .build();

        //lap qua Map de set quantity
        for (Map.Entry<Variant, Double> entry: variantRequestSet.entrySet()){
            Variant variant = entry.getKey();
            Double quantity = entry.getValue();

            WallpaperVariant temp = WallpaperVariant.builder()
                    .wallpaper(wallpaper)
                    .variant(variant)
                    .quantity(quantity)
                    .build();
            wallpaper.getWallpaperVariants().add(temp);
        }
        return wallpaper;
    }
    public static WallpaperResponse fromWallpaperEntity(Wallpaper wallpaper){

        return WallpaperResponse.builder()
                .area(String.valueOf(wallpaper.getArea()))
                .variants(convertVariantToVResponse(wallpaper.getWallpaperVariants()))
                .build();
    }
    public static Wallpaper fromWallpaperEntity(String area, Map<Variant, Double> variantRequestSet, Wallpaper wallpaper){
        wallpaper.setArea(Double.parseDouble(area));
        Set<WallpaperVariant> existingWallpaperVariants = wallpaper.getWallpaperVariants();

        Set<WallpaperVariant> updatedWallpaperVariants = new HashSet<>();
        for (Map.Entry<Variant, Double> entry: variantRequestSet.entrySet()){
            Variant variant = entry.getKey();
            Double quantity = entry.getValue();

            WallpaperVariant wallpaperVariant = existingWallpaperVariants.stream()
                    .filter(wv -> wv.getVariant().equals(variant))
                    .findFirst()
                    .orElse(null);

            if (wallpaperVariant == null){
                wallpaperVariant = WallpaperVariant.builder()
                        .wallpaper(wallpaper)
                        .variant(variant)
                        .quantity(quantity)
                        .build();
                updatedWallpaperVariants.add(wallpaperVariant);
            }else {
                wallpaperVariant.setQuantity(quantity);
                updatedWallpaperVariants.add(wallpaperVariant);
            }
        }
        existingWallpaperVariants.removeIf(wv -> !variantRequestSet.containsKey(wv.getVariant()));
        wallpaper.setWallpaperVariants(updatedWallpaperVariants);
        return wallpaper;
    }

}
