package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.WallpaperResponse;
import com.dcode.product_service.entity.Wallpaper;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.ProductRepository;
import com.dcode.product_service.repository.VariantRepository;
import com.dcode.product_service.repository.WallpaperRepository;
import com.dcode.product_service.service.IWallpaperService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static com.dcode.product_service.utils.WallpaperUtils.createNewWallpaperEntity;
import static com.dcode.product_service.utils.WallpaperUtils.fromWallpaperEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
@Slf4j
public class WallpaperServiceImpl implements IWallpaperService {

    private final WallpaperRepository wallpaperRepository;
    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;

//    public void createAWallpaper(String productId, String area, Set<VariantRequest> variantRequestSet) {
//        wallpaperRepository.save(createAWallpaperEntity(productId, area, variantRequestSet));
//
//    }

//    private Wallpaper createAWallpaperEntity(String productId, String area, Set<VariantRequest> variantRequestSet) {
//        var product = productRepository.findByProductId(productId).orElseThrow(()->new ApiException("Product not found while create new wallpaper!"));
//        var currentVariants = variantRepository.findByWallpaperIsNotNullAndPaintIsNullAndFloorIsNull();
//        return createNewWallpaperEntity(product, area, variantRequestSet, currentVariants);
//    }

    @Override
    public WallpaperResponse getAWallpaper(String wallpaperId) {
        var wallpaper = wallpaperRepository.findByWallpaperId(wallpaperId).orElseThrow(()-> new ApiException("Wallpaper not found!"));
        return fromWallpaperEntity(wallpaper);
    }
}
