package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.dtoResponse.ColorFamilyResponse;
import com.dcode.product_service.entity.Collection;
import com.dcode.product_service.entity.ColorFamily;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.CollectionUtils.fromCollectionEntity;

public class ColorFamilyUtils {
    public static ColorFamily createNewACFEntity(String name, String title, String description, String hex, String image){
        return ColorFamily.builder()
                .colorFamilyId(UUID.randomUUID().toString())
                .name(name)
                .title(title)
                .description(description)
                .hex(hex)
                .image(image)
//                .collections(collectionSet) //Should we allow set collection there or while create collection.
                .build();
    }
    public static ColorFamilyResponse fromColorFamilyEntity(ColorFamily colorFamily){
        Set<CollectionResponse> collectionSet = colorFamily.getCollections().stream()
                .map(collection -> {
                    CollectionResponse collectionResponse = fromCollectionEntity(collection);
//                    if FE no need these, community with team :V
//                    collectionResponse.setColorFamily(null);
//                    collectionResponse.setRoom(null);
//                    collectionResponse.setRelativeCollection(null);
                    return collectionResponse;
                })
                .collect(Collectors.toSet());
        return ColorFamilyResponse.builder()
                .colorFamilyId(colorFamily.getColorFamilyId())
                .name(colorFamily.getName())
                .title(colorFamily.getTitle())
                .description(colorFamily.getDescription())
                .hex(colorFamily.getHex())
                .image(colorFamily.getImage())
                .collections(collectionSet)
                .build();
    }
}
