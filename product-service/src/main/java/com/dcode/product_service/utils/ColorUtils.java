package com.dcode.product_service.utils;

import com.dcode.product_service.entity.Color;

import java.util.UUID;

public class ColorUtils {
    public static Color createNewColorEntity(String name, String code, String description){
        return Color.builder()
                .colorId(UUID.randomUUID().toString())
                .name(name)
                .code(code)
                .description(description)
                .build();
    }
}
