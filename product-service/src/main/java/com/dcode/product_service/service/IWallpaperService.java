package com.dcode.product_service.service;

import com.dcode.product_service.dtoResponse.WallpaperResponse;

import java.util.Optional;

public interface IWallpaperService {
    WallpaperResponse getAWallpaper(String wallpaperId);
}
