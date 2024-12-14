package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.WallpaperRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.WallpaperServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/wallpapers")
@AllArgsConstructor
public class WallpaperController {

    private final WallpaperServiceImpl wallpaperService;

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @PostMapping("/productId/{productId}")
    public ResponseEntity<Response> createAWallpaper(@PathVariable("productId") String productId, @RequestBody @Valid WallpaperRequest wallpaperRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
        wallpaperService.createWallpaper(productId, wallpaperRequest);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Wallpaper created successfully!", CREATED));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @PostMapping
    public ResponseEntity<Response> createWallpapers(@RequestBody @Valid Set<WallpaperRequest> wallpaperRequests, HttpServletRequest request, HttpServletResponse response) {
        try {
            wallpaperService.createWallpapers(wallpaperRequests);
            return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Wallpaper created successfully!", CREATED));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public/wallpaperId/{wallpaperId}")
    public ResponseEntity<Response> getAWallpaper(@PathVariable("wallpaperId") String wallpaperId, HttpServletRequest request, HttpServletResponse response) {
        try {
        var wallpaper = wallpaperService.getAWallpaper(wallpaperId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("wallpaper", wallpaper), "Retrieve wallpaper successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @PutMapping("/wallpaperId/{wallpaperId}")
    public ResponseEntity<Response> updateAWallpaper(@PathVariable("wallpaperId") String wallpaperId, @RequestBody WallpaperRequest wallpaperRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
        wallpaperService.updateAWallpaper(wallpaperId, wallpaperRequest.getArea(), wallpaperRequest.getVariants());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Update Wallpaper successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @DeleteMapping("/wallpaperId/   {wallpaperId}")
    public ResponseEntity<Response> deleteAWallpaper(@PathVariable("wallpaperId") String wallpaperId, HttpServletRequest request, HttpServletResponse response) {
        try {
        wallpaperService.deleteAWallpaper(wallpaperId);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Wallpaper deleted successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<Response> getAllWallpaperPageable(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) {
        try {
        Pageable pageable = PageRequest.of(page, size);
        var wallpapers = wallpaperService.getAllWallpaperPageable(pageable);
        return ResponseEntity.ok().body(getResponse(request, Map.of("wallpapers", wallpapers), "Wallpaper retrieve successfullt!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    private URI getUri() {
        return URI.create("");
    }


}
