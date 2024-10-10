package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.CollectionRequest;
import com.dcode.product_service.dtoResponse.*;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.exception.ApiException;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollectionUtils {
    public static Collection createNewCollectionEntity(CollectionRequest collectionRequest, Set<Color> colors, ColorFamily colorFamily, Room room, CollectionType collectionType, RelativeCollection relativeCollection) {
        Collection collection = Collection.builder()
                .collectionId(UUID.randomUUID().toString())
                .name(collectionRequest.getName())
                .title(collectionRequest.getTitle())
                .description(collectionRequest.getDescription())
                .image(collectionRequest.getImage())
                .hex(collectionRequest.getHex())
                .colors(colors)
                .colorFamily(colorFamily)
                .room(room)
                .collectionType(collectionType)
                .relativeCollection(relativeCollection)
                .build();
        for (Color color : colors) {
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
    public static CollectionResponse fromCollectionEntityColorOnly (Collection collection){
        Set<ColorResponse> colorResponseSet = collection.getColors().stream()
                .map(color -> ColorResponse.builder()
                        .colorId(color.getColorId())
                        .name(color.getName())
                        .hex(color.getHex())
                        .code(color.getCode())
                        .build())
                .collect(Collectors.toSet());
        return CollectionResponse.builder()
                .colors(colorResponseSet)
                .build();
    }
    public static CollectionResponse fromCollectionBasicEntity(Collection collection){
        Set<ColorResponse> colorResponseSet = collection.getColors().stream()
                .map(ColorUtils::fromColorEntityPartical)
                .limit(5)
                .collect(Collectors.toSet());

        return CollectionResponse.builder()
                .collectionId(collection.getCollectionId())
                .name(collection.getName())
                .title(collection.getTitle())
                .description(collection.getDescription())
                .image(collection.getImage())
                .hex(collection.getHex())
                .colors(colorResponseSet)
//                .colorFamily()
                .build();
    }
    public static CollectionResponse fromCollectionEntity(Collection collection) {
        Set<ColorResponse> colorResponseSet = collection.getColors().stream()
                .map(color -> ColorResponse.builder()
                        .colorId(color.getColorId())
                        .name(color.getName())
                        .code(color.getCode())
                        .hex(color.getHex())
                        .description(color.getDescription())
                        .build())
                .collect(Collectors.toSet());
        return CollectionResponse.builder()
                .collectionId(collection.getCollectionId())
                .name(collection.getName())
                .title(collection.getTitle())
                .description(collection.getDescription())
                .image(collection.getImage())
                .hex(collection.getHex())
                .colors(colorResponseSet)
                .colorFamily(collection.getColorFamily() != null
                        ? ColorFamilyResponse.builder()
                        .colorFamilyId(collection.getColorFamily().getColorFamilyId())
                        .name(collection.getColorFamily().getName())
                        .description(collection.getColorFamily().getDescription())
                        .build()
                        : null)
                .room(collection.getRoom() != null
                        ? RoomResponse.builder()
                        .roomId(collection.getRoom().getRoomId())
                        .roomType(collection.getRoom().getRoomType())
                        .image(collection.getRoom().getImage())
                        .textUrl3D(collection.getRoom().getTextUrl3D())
                        .build()
                        : null)
                .relativeCollection(collection.getRelativeCollection() != null
                        ? RelativeCollectionResponse.builder()
                        .relativeCollectionId(collection.getRelativeCollection().getRelativeCollectionId())
                        .name(collection.getRelativeCollection().getName())
                        .build()
                        : null)
                .build();
    }

}
