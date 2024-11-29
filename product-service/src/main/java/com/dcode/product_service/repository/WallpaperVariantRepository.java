package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Wallpaper;
import com.dcode.product_service.entity.WallpaperVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WallpaperVariantRepository extends JpaRepository<WallpaperVariant, Long> {
    Optional<WallpaperVariant> findByWallpaper_Product_productIdAndVariant_sizeNameAndVariant_categoryName(String productId, String sizeName, String categoryName);
    Optional<WallpaperVariant> findByWallpaper_wallpaperIdAndVariant_variantId(String wallpaperId, String variantId);

    void deleteByWallpaper (Wallpaper wallpaper);
}
