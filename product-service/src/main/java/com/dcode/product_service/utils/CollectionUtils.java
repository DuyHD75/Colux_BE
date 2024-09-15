package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.*;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.exception.ApiException;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollectionUtils {
    public static Collection createNewCollectionEntity(String name, Set<Color> colors, ColorFamily colorFamily, Room room, CollectionType collectionType, RelativeCollection relativeCollection){
        Collection collection = Collection.builder()
                .collectionId(UUID.randomUUID().toString())
                .name(name)
                .colors(colors)
                .colorFamily(colorFamily)
                .room(room)
                .collectionType(collectionType)
                .relativeCollection(relativeCollection)
                .build();
        for (Color color: colors){
            color.getCollections().add(collection);
        }
        return collection;
    }
    public static Set<Color> getColorsFromNames(Set<String> colorNames, Set<Color> foundColors) {
        Set<String> foundColorNames = foundColors.stream()
                .map(Color::getColorId)
                .collect(Collectors.toSet());

        Set<String> missingColors = colorNames.stream()
                .filter(name -> !foundColorNames.contains(name))
                .collect(Collectors.toSet());

        if (!missingColors.isEmpty()) {
            throw new ApiException("The following colors were not found: " + missingColors);
        }

        return foundColors;
    }
    public static CollectionResponse fromCollectionEntity(Collection collection){
        Set<ColorResponse> colorResponseSet = collection.getColors().stream()
                .map(color -> ColorResponse.builder()
                        .name(color.getName())
                                .code(color.getCode())
                                .description(color.getDescription())
                                .build())
                .collect(Collectors.toSet());
        ColorFamilyResponse colorFamilyResponse = ColorFamilyResponse.builder()
                .colorFamilyId(collection.getColorFamily().getColorFamilyId())
                .name(collection.getColorFamily().getName())
                .description(collection.getColorFamily().getDescription())
                .build();
        RoomResponse roomResponse = RoomResponse.builder()
                .roomType(collection.getRoom().getRoomType())
                .image(collection.getRoom().getImage())
                .textUrl3D(collection.getRoom().getTextUrl3D())
                .build();
        RelativeCollectionResponse relativeCollectionResponse = RelativeCollectionResponse.builder()
                .name(collection.getRelativeCollection().getName())
                .build();
        return CollectionResponse.builder()
                .name(collection.getName())
                .colors(colorResponseSet)
                .colorFamily(colorFamilyResponse)
                .room(roomResponse)
                .relativeCollection(relativeCollectionResponse)
                .build();


    }
}
