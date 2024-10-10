package com.dcode.product_service.service;

import com.dcode.product_service.dtoResponse.CategoryResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.PageResponse;
import com.dcode.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    void createCategory(String name, String thumbnail);

    List<CategoryResponse> getAllCategory();

    CategoryResponse getCategoryByCategoryId(String categoryId);

    PageResponse<ProductResponse> getAllProductByCategoryId(String categoryId, Pageable page);
}
