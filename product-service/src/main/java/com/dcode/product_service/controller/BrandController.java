package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.BrandRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.BrandServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/products/brands")
@AllArgsConstructor
public class BrandController {

    private final BrandServiceImpl brandService;

    @GetMapping("/test")
    public ResponseEntity<Response> TestBrand(HttpServletRequest request){
        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(),
                        "Brand created successfully!", CREATED)
        );
    }


    @PostMapping
    public ResponseEntity<Response> createBrand(@RequestBody @Valid BrandRequest brandRequest, HttpServletRequest request, HttpServletResponse response){
        try {
            brandService.createBrand(brandRequest.getName(), brandRequest.getCode(), brandRequest.getStatus());
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, emptyMap(),
                            "Brand created successfully!", CREATED)
            );
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
