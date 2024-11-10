package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoRequest.WallpaperRequest;
import com.dcode.product_service.dtoResponse.WallpaperResponse;
import com.dcode.product_service.entity.PageResponse;
import com.dcode.product_service.entity.PageResponseBuilder;
import com.dcode.product_service.entity.Wallpaper;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.ProductRepository;
import com.dcode.product_service.repository.VariantRepository;
import com.dcode.product_service.repository.WallpaperRepository;
import com.dcode.product_service.repository.WallpaperVariantRepository;
import com.dcode.product_service.service.IWallpaperService;
import com.dcode.product_service.utils.WallpaperUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.PaintUtils.checkVariantRequestSet;
import static com.dcode.product_service.utils.PaintUtils.extractVariantIds;
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
    private final WallpaperVariantRepository wallpaperVariantRepository;

    public void createWallpaper(String productId, WallpaperRequest wallpaperRequest) {
        wallpaperRepository.save(createAWallpaperEntity(productId, wallpaperRequest));
    }
    public void createWallpapers(Set<WallpaperRequest> wallpaperRequests) {
        wallpaperRepository.saveAll(wallpaperRequests.stream().map(
                wallpaperRequest -> createAWallpaperEntity(wallpaperRequest.getProductId(), wallpaperRequest)
                ).collect(Collectors.toSet()));
    }

    private Wallpaper createAWallpaperEntity(String productId, WallpaperRequest wRequest) {
        var product = productRepository.findByProductId(productId).orElseThrow(()->new ApiException("Product not found while create new wallpaper!"));
        Set<String> variantIds = extractVariantIds(wRequest.getVariants());
        return createNewWallpaperEntity(product, wRequest.getArea(), checkVariantRequestSet(wRequest.getVariants(), variantRepository.findAllByVariantIdIn(variantIds)));
    }

    @Override
    public WallpaperResponse getAWallpaper(String wallpaperId) {
        var wallpaper = wallpaperRepository.findByWallpaperId(wallpaperId).orElseThrow(()-> new ApiException("Wallpaper not found!"));
        return fromWallpaperEntity(wallpaper);
    }

    @Override
    public void updateAWallpaper(String wallpaperId, String area, Set<VariantRequest> variantRequestSet) {
        var wallpaper = wallpaperRepository.findByWallpaperId(wallpaperId).orElseThrow(()-> new ApiException("Wallpaper not found while update!"));
        Set<String> variantIds = extractVariantIds(variantRequestSet);
        var wallpaperUpdated = fromWallpaperEntity(checkVariantRequestSet(variantRequestSet, variantRepository.findAllByVariantIdIn(variantIds)), wallpaper);
        wallpaperRepository.save(wallpaperUpdated);
    }

    @Override
    public void deleteAWallpaper(String wallpaperId) {
        var wallpaper = wallpaperRepository.findByWallpaperId(wallpaperId).orElseThrow(() -> new ApiException("Wallpaper not found while delete!"));
        wallpaperVariantRepository.deleteByWallpaper(wallpaper);
        wallpaperRepository.delete(wallpaper);

    }

    @Override
    public PageResponse<WallpaperResponse> getAllWallpaperPageable(Pageable pageable) {
        var wallpapers = wallpaperRepository.findAll(pageable);
        if (wallpapers.isEmpty()) throw new ApiException("Wallpaper empty!");
        Page<WallpaperResponse> wallpaperResponsePage = wallpapers.map(WallpaperUtils::fromWallpaperEntity);
        return PageResponseBuilder.buildPageResponse(wallpaperResponsePage);
    }
}
