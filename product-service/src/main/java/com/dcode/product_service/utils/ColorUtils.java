package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.ColorRequest;
import com.dcode.product_service.dtoResponse.ColorResponse;
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

    public static ColorResponse fromColorEntity(Color color){
        return ColorResponse.builder()
                .name(color.getName())
                .code(color.getCode())
                .description(color.getDescription())
                .build();
    }

    public static Color updateColorEntity(Color color, ColorRequest colorRequest){
        color.setName(colorRequest.getName());
        color.setCode(colorRequest.getCode());
        color.setDescription(colorRequest.getDescription());
        return color;
    }
}

