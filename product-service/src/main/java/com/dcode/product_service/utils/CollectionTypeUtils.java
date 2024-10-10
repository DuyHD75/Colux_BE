package com.dcode.product_service.utils;

import com.dcode.product_service.entity.CollectionType;

import java.util.UUID;

public class CollectionTypeUtils {
    public static CollectionType createNewACT(String name){
        return CollectionType.builder()
                .collectionTypeId(UUID.randomUUID().toString())
                .name(name)
                .build();
    }
}
