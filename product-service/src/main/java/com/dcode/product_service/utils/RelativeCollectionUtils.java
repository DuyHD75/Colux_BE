package com.dcode.product_service.utils;

import com.dcode.product_service.entity.RelativeCollection;

import java.util.UUID;

public class RelativeCollectionUtils {
    public static RelativeCollection createNewRelativeCollection (String name){
        return RelativeCollection.builder()
                .relativeCollectionId(UUID.randomUUID().toString())
                .name(name)
                .build();
    }
}
