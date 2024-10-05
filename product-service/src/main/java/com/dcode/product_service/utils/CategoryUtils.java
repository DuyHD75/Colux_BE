package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.CategoryResponse;
import com.dcode.product_service.entity.Category;

import java.util.UUID;

public class CategoryUtils {
    public static Category createNewCategoryEntity(String name, String thumbnail){
        return Category.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(name)
                .thumbnail(thumbnail)
                .build();
    }
    public static CategoryResponse fromCategoryEntity(Category category){
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .thumbnail(category.getThumbnail())
                .build();
    }

}
