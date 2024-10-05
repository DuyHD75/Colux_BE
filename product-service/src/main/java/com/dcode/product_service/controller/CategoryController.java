package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.CategoryRequest;
import com.dcode.product_service.service.impl.CategoryServiceImpl;
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
@RequestMapping("/api/v1/products/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    @PostMapping
    public ResponseEntity<Response> createNewCategory(@RequestBody @Valid CategoryRequest categoryRequest, HttpServletRequest request) {
        categoryService.createCategory(categoryRequest.getName(), categoryRequest.getThumbnail());
        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(),
                        "Category created successfully!", CREATED)
        );
    }

    @GetMapping
    public ResponseEntity<Response> getAllCategory(HttpServletRequest request) {
        var categories = categoryService.getAllCategory();
        return ResponseEntity.ok().body(getResponse(request, Map.of("categories", categories), "Retrieve categories successfully!", OK));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Response> getCategoryByCategoryId(@PathVariable("categoryId") String categoryId, HttpServletRequest request) {
        var category = categoryService.getCategoryByCategoryId(categoryId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("category", category), "Retrieve category successfully!", OK));
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<Response> getProductByCategory(@PathVariable("categoryId") String categoryId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page,size);
        var products = categoryService.getAllProductByCategoryId(categoryId, pageable);
        return ResponseEntity.ok().body(getResponse(request, Map.of("products", products), "Product by category retrieve successfully!", OK));
    }

    private URI getUri() {
        return URI.create("");
    }

}
