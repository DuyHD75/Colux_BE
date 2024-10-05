package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.WallpaperResponse;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface IWallpaperService {
    WallpaperResponse getAWallpaper(String wallpaperId);

    void updateAWallpaper(String wallpaperId, String area, Set<VariantRequest> variants);

    void deleteAWallpaper(String wallpaperId);

    PageResponse<WallpaperResponse> getAllWallpaperPageable(Pageable pageable);
}
