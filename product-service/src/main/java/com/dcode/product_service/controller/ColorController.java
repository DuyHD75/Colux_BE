package com.dcode.product_service.controller;


import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.ColorRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.ColorServiceImpl;
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
import java.util.List;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/colors")
@AllArgsConstructor
public class ColorController {

    private final ColorServiceImpl colorService;

    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:create')")
    @PostMapping()
    public ResponseEntity<Response> createAColor(@RequestBody @Valid List<ColorRequest> colorRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            colorService.createColors(colorRequest);
            return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Color created successfully!", CREATED));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public/colorId/{colorId}")
    public ResponseEntity<Response> getAColor(@PathVariable("colorId") String colorId, HttpServletRequest request, HttpServletResponse response) {
        try {
            var color = colorService.getAColor(colorId);
            return ResponseEntity.ok().body(getResponse(request, Map.of("color", color), "Color retrieve successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }
    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:update')")
    @PutMapping("/colorId/{colorId}")
    public ResponseEntity<Response> updateAColor(@PathVariable("colorId") String colorId, @RequestBody ColorRequest colorRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            colorService.updateAColor(colorId, colorRequest);
            return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Color update successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }
    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:delete')")
    @DeleteMapping("/colorId/{colorId}")
    public ResponseEntity<Response> deleteAColor(@PathVariable("colorId") String colorId, HttpServletRequest request, HttpServletResponse response) {
        try {
            colorService.deleteAColor(colorId);
            return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Color deleted successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<Response> getAllColor(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            var colors = colorService.getAllColor(pageable);
            return ResponseEntity.ok().body(getResponse(request, Map.of("colors", colors), "Retrieve all color successfully", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public/color-family/{colorFamilyId}/collection/{collectionId}")
    public ResponseEntity<Response> getColorByColorFamilyAndCollection(@PathVariable("colorFamilyId") String colorFamilyId,
                                                                       @PathVariable("collectionId") String collectionId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       HttpServletRequest request,
                                                                       HttpServletResponse response) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            var colors = colorService.getColorByColorFamilyAndCollection(collectionId, colorFamilyId, pageable);
            return ResponseEntity.ok().body(getResponse(request, Map.of("colors", colors), "Retrieve colors by color family and collection successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }
    @GetMapping("/public/collection/{collectionId}/room/{roomId}")
    public ResponseEntity<Response> getColorByCollectionAndRoomId(@PathVariable("collectionId") String collectionId,
                                                                  @PathVariable("roomId")String roomId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  HttpServletRequest request,
                                                                  HttpServletResponse response){
        try {
            Pageable pageable = PageRequest.of(page, size);
            var colors = colorService.getColorByCollectionAndRoom(collectionId, roomId, pageable);
            return ResponseEntity.ok().body(getResponse(request, Map.of("colors", colors), "Retrieve colors by collection and room successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public/getColor")
    public ResponseEntity<Response> getColor(
            @RequestParam(value = "interior", required = false) Boolean interior,
            @RequestParam(value = "exterior", required = false) Boolean exterior,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            var colors = colorService.getColor(interior, exterior, pageable);
            return ResponseEntity.ok().body(getResponse(request, Map.of("colors", colors), "Color retrieve successfully!", OK));
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
