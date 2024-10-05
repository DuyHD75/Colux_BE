package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Wallpaper;
import com.dcode.product_service.entity.WallpaperVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WallpaperVariantRepository extends JpaRepository<WallpaperVariant, Long> {
    void deleteByWallpaper (Wallpaper wallpaper);
}
