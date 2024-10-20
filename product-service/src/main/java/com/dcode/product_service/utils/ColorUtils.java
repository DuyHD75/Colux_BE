package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.ColorRequest;
import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.dtoResponse.ColorFamilyResponse;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.Collection;
import com.dcode.product_service.entity.Color;
import com.dcode.product_service.entity.ColorFamily;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.CollectionUtils.fromCollectionEntity;
import static com.dcode.product_service.utils.ColorFamilyUtils.fromColorFamilyEntity;

public class ColorUtils {
    public static Color createNewColorEntity(ColorRequest colorRequest) {
        return Color.builder()
                .colorId(UUID.randomUUID().toString())
                .name(colorRequest.getName())
                .image(colorRequest.getImage())
                .LRV(colorRequest.getLRV())
                .interior(colorRequest.isInterior())
                .exterior(colorRequest.isExterior())
                .code(colorRequest.getCode())
                .hex(colorRequest.getHex())
                .description(colorRequest.getDescription())
                .colorTypeId(colorRequest.getColorTypeId())
                .build();
    }

    public static ColorResponse fromColorEntity(Color color) {
        Set<ColorFamily> colorFamily = color.getCollections().stream()
                .map(Collection::getColorFamily)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<CollectionResponse> collections = color.getCollections().stream()
                .map(collection -> {
                    CollectionResponse response = fromCollectionEntity(collection);
//                    if FE no need these, community with team :V
//                    collectionResponse.setColorFamily(null);
//                    collectionResponse.setRoom(null);
//                    collectionResponse.setRelativeCollection(null);
                    return response;
                }).collect(Collectors.toSet());
        Set<ColorFamilyResponse> colorFamilyResponses = colorFamily.stream()
                .map(ColorFamilyUtils::fromColorFamilyEntity).collect(Collectors.toSet());
        return ColorResponse.builder()
                .name(color.getName())
                .image(color.getImage())
                .code(color.getCode())
                .hex(color.getHex())
                .LRV(color.getLRV())
                .interior(color.isInterior())
                .exterior(color.isExterior())
                .description(color.getDescription())
                .colorTypeId(color.getColorTypeId())
                .collections(collections)
                .colorFamily(colorFamilyResponses)
                .build();
    }
    public static ColorResponse fromColorEntityPartical(Color color){
        Set<ColorFamily> colorFamilies = color.getCollections().stream()
                .map(Collection::getColorFamily)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<CollectionResponse> collections = color.getCollections().stream()
                .map(collection -> CollectionResponse.builder().collectionId(collection.getCollectionId()).build())
                .collect(Collectors.toSet());
        Set<ColorFamilyResponse> colorFamilyResponses = colorFamilies.stream()
                .map(colorFamily -> ColorFamilyResponse.builder().colorFamilyId(colorFamily.getColorFamilyId()).build())
                .collect(Collectors.toSet());
        return ColorResponse.builder()
                .colorId(color.getColorId())
                .name(color.getName())
                .image(color.getImage())
                .code(color.getCode())
                .LRV(color.getLRV())
                .hex(color.getHex())
                .interior(color.isInterior())
                .exterior(color.isExterior())
                .description(color.getDescription())
                .colorTypeId(color.getColorTypeId())
                .collections(collections)
                .colorFamily(colorFamilyResponses)
                .build();
    }
    public static Color updateColorEntity(Color color, ColorRequest colorRequest) {
        color.setName(colorRequest.getName());
        color.setCode(colorRequest.getCode());
        color.setHex(colorRequest.getHex());
        color.setDescription(colorRequest.getDescription());
        return color;
    }
    public static ColorResponse simpleColorResponse(Color color){
        return ColorResponse.builder()
                .hex(color.getHex())
                .name(color.getName())
                .colorId(color.getColorId())
                .code(color.getCode())
                .build();

    }
}

