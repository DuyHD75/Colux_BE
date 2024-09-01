package com.dcode.product_service.utils;

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
}
