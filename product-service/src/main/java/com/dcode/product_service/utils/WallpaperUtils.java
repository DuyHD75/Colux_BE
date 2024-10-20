package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.VariantResponse;
import com.dcode.product_service.dtoResponse.WallpaperResponse;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.entity.Variant;
import com.dcode.product_service.entity.Wallpaper;
import com.dcode.product_service.entity.WallpaperVariant;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.PaintUtils.convertVariantToVResponse;
import static com.dcode.product_service.utils.ProductUtils.fromProductEntitySimple;

public class WallpaperUtils {
    public static Wallpaper createNewWallpaperEntity(Product product, String area, Map<Variant, Pair<Integer, Double>> variantRequestSet){
        Set< WallpaperVariant> wallpaperVariants = new HashSet<>();
        Wallpaper wallpaper = Wallpaper.builder()
                .wallpaperId(UUID.randomUUID().toString())
                .product(product)
                .wallpaperVariants(wallpaperVariants)
                .build();

        //lap qua Map de set quantity
        for (Map.Entry<Variant, Pair<Integer, Double>> entry: variantRequestSet.entrySet()){
            Variant variant = entry.getKey();
            Integer quantity = entry.getValue().getLeft();
            Double price = entry.getValue().getRight();

            WallpaperVariant temp = WallpaperVariant.builder()
                    .wallpaperVariantId(UUID.randomUUID().toString())
                    .wallpaper(wallpaper)
                    .variant(variant)
                    .quantity(quantity)
                    .price(price)
                    .build();
            wallpaper.getWallpaperVariants().add(temp);
        }
        return wallpaper;
    }
    public static WallpaperResponse fromWallpaperEntity(Wallpaper wallpaper){

        return WallpaperResponse.builder()
                .wallpaperId(wallpaper.getWallpaperId())
                .variants(convertVariantToVResponse(wallpaper.getWallpaperVariants()))
                .product(fromProductEntitySimple(wallpaper.getProduct()))
                .build();
    }
    public static Wallpaper fromWallpaperEntity(Map<Variant, Pair<Integer, Double>> variantRequestSet, Wallpaper wallpaper){
        Set<WallpaperVariant> existingWallpaperVariants = wallpaper.getWallpaperVariants();
        Set<WallpaperVariant> updatedWallpaperVariants = new HashSet<>();
        for (Map.Entry<Variant, Pair<Integer, Double>> entry: variantRequestSet.entrySet()){
            Variant variant = entry.getKey();
            Integer quantity = entry.getValue().getLeft();
            Double price = entry.getValue().getRight();

            WallpaperVariant wallpaperVariant = existingWallpaperVariants.stream()
                    .filter(wv -> wv.getVariant().equals(variant))
                    .findFirst()
                    .orElse(null);

            if (wallpaperVariant == null){
                wallpaperVariant = WallpaperVariant.builder()
                        .wallpaper(wallpaper)
                        .variant(variant)
                        .quantity(quantity)
                        .price(price)
                        .build();
                updatedWallpaperVariants.add(wallpaperVariant);
            }else {
                wallpaperVariant.setQuantity(quantity);
                wallpaperVariant.setPrice(price);
                updatedWallpaperVariants.add(wallpaperVariant);
            }
        }
        existingWallpaperVariants.removeIf(wv -> !variantRequestSet.containsKey(wv.getVariant()));
        wallpaper.setWallpaperVariants(updatedWallpaperVariants);
        return wallpaper;
    }

}
