package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.CategoryRequest;
import com.dcode.product_service.service.impl.CategoryServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    @PostMapping
    public ResponseEntity<Response> createNewCategory(@RequestBody @Valid CategoryRequest categoryRequest, HttpServletRequest request){
        categoryService.createCategory(categoryRequest.getName(), categoryRequest.getThumbnail());
        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(),
                        "Category created successfully!", CREATED)
        );
    }

    private URI getUri() {
        return URI.create("");
    }

}
