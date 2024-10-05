package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.WallpaperRequest;
import com.dcode.product_service.service.impl.WallpaperServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/products/wallpapers")
@AllArgsConstructor
public class WallpaperController {

    private final WallpaperServiceImpl wallpaperService;

    @PostMapping("/{productId}")
    public ResponseEntity<Response> createAWallpaper(@PathVariable("productId") String productId, @RequestBody @Valid WallpaperRequest wallpaperRequest, HttpServletRequest request) {
        wallpaperService.createAWallpaper(productId, wallpaperRequest);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Wallpaper created successfully!", CREATED));
    }

    @GetMapping("{wallpaperId}")
    public ResponseEntity<Response> getAWallpaper(@PathVariable("wallpaperId") String wallpaperId, HttpServletRequest request) {
        var wallpaper = wallpaperService.getAWallpaper(wallpaperId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("wallpaper", wallpaper), "Retrieve wallpaper successfully!", OK));
    }

    @PutMapping("{wallpaperId}")
    public ResponseEntity<Response> updateAWallpaper(@PathVariable("wallpaperId") String wallpaperId, @RequestBody WallpaperRequest wallpaperRequest, HttpServletRequest request) {
        wallpaperService.updateAWallpaper(wallpaperId, wallpaperRequest.getArea(), wallpaperRequest.getVariants());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Update Wallpaper successfully!", OK));
    }

    @DeleteMapping("{wallpaperId}")
    public ResponseEntity<Response> deleteAWallpaper(@PathVariable("wallpaperId") String wallpaperId, HttpServletRequest request) {
        wallpaperService.deleteAWallpaper(wallpaperId);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Wallpaper deleted successfully!", OK));
    }

    @GetMapping
    public ResponseEntity<Response> getAllWallpaperPageable(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        var wallpapers = wallpaperService.getAllWallpaperPageable(pageable);
        return ResponseEntity.ok().body(getResponse(request, Map.of("wallpapers", wallpapers), "Wallpaper retrieve successfullt!", OK));
    }

    private URI getUri() {
        return URI.create("");
    }


}
