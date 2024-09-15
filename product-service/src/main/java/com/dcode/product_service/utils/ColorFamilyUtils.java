package com.dcode.product_service.utils;

import com.dcode.product_service.entity.ColorFamily;

import java.util.UUID;

public class ColorFamilyUtils {
    public static ColorFamily createNewACFEntity(String name, String description){
        return ColorFamily.builder()
                .ColorFamilyId(UUID.randomUUID().toString())
                .name(name)
                .description(description)
                .build();
    }
}
