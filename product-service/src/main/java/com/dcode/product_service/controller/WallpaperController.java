package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.WallpaperRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.WallpaperServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/wallpapers")
@AllArgsConstructor
public class WallpaperController {

    private final WallpaperServiceImpl wallpaperService;

//    @PostMapping("/{productId}")
//    public ResponseEntity<Response> createAWallpaper(@PathVariable("productId")String productId, @RequestBody @Valid WallpaperRequest wallpaperRequest, HttpServletRequest request){
//        wallpaperService.createAWallpaper(productId, wallpaperRequest.getArea(), wallpaperRequest.getVariants());
//        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(),"Wallpaper created successfully!", CREATED));
//    }
//    @GetMapping("{wallpaperId}")
//    public ResponseEntity<Response> getAWallpaper(@PathVariable("wallpaperId")String wallpaperId, HttpServletRequest request){
//        var wallpaper = wallpaperService.getAWallpaper(wallpaperId);
//        return ResponseEntity.ok().body(getResponse(request,Map.of("wallpaper", wallpaper),"Retrieve wallpaper successfully!", OK));
//    }
    private URI getUri(){
        return URI.create("");
    }



}
