package com.dcode.product_service.controller;


import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.ImageRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.ImageServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class ImageController {

    private final ImageServiceImpl imageService;
    // TODO: add ADMIN
    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:create')")
    @PostMapping
    public ResponseEntity<Response> createAImage(@RequestBody @Valid ImageRequest imageRequest, HttpServletRequest request, HttpServletResponse response){
        try {
        imageService.createAImage(imageRequest);
        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(), "Image created successfully!", CREATED));
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
