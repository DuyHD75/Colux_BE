package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.CategoryRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.CategoryServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/products/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    @GetMapping("/test/{testId}")
    public ResponseEntity<Response> test(@PathVariable("testId") String testId, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok().body(getResponse(request, Map.of("testId", testId), "Test successfully!", OK));
    }

    @PostMapping
    public ResponseEntity<Response> createNewCategory(@RequestBody @Valid CategoryRequest categoryRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            categoryService.createCategory(categoryRequest.getName(), categoryRequest.getThumbnail());
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, emptyMap(),
                            "Category created successfully!", CREATED)
            );
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }


    }

    @GetMapping("/getAll")
    public ResponseEntity<Response> getAllCategory(HttpServletRequest request, HttpServletResponse response) {
        try {
            var categories = categoryService.getAllCategory();
            return ResponseEntity.ok().body(getResponse(request, Map.of("categories", categories), "Retrieve categories successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }

    @GetMapping("/categoryId/{categoryId}")
    public ResponseEntity<Response> getCategoryByCategoryId(@PathVariable("categoryId") String categoryId, HttpServletRequest request, HttpServletResponse response) {
        try {
            var category = categoryService.getCategoryByCategoryId(categoryId);
            return ResponseEntity.ok().body(getResponse(request, Map.of("category", category), "Retrieve category successfully!", OK));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<Response> getProductByCategory(@PathVariable("categoryId") String categoryId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        try{
            Pageable pageable = PageRequest.of(page,size);
            var products = categoryService.getAllProductByCategoryId(categoryId, pageable);
            return ResponseEntity.ok().body(getResponse(request, Map.of("products", products), "Product by category retrieve successfully!", OK));
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
