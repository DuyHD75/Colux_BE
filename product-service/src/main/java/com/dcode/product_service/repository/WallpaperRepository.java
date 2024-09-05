package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Wallpaper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WallpaperRepository extends JpaRepository<Wallpaper, Long> {
    Optional<Wallpaper> findByWallpaperId(String wallpaperId);

}
