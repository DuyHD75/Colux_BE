package com.dcode.product_service.utils;

import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.dtoResponse.RoomResponse;
import com.dcode.product_service.entity.Room;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.CollectionUtils.fromCollectionEntity;

public class RoomUtils {
    public static Room createNewRoomEntity(String roomType, String hex, String title, String description, String image, String textUrl3D){
        return Room.builder()
                .roomId(UUID.randomUUID().toString())
                .roomType(roomType)
                .hex(hex)
                .title(title)
                .description(description)
                .image(image)
                .textUrl3D(textUrl3D)
                .build();
    }

    public static RoomResponse fromRoomEntity(Room room){
        Set<CollectionResponse> collections = room.getCollections().stream()
                .map(collection -> {
                    CollectionResponse response = fromCollectionEntity(collection);
                    //                    if FE no need these, community with team :V
//                    collectionResponse.setColorFamily(null);
//                    collectionResponse.setRoom(null);
//                    collectionResponse.setRelativeCollection(null);
                    return response;
                }).collect(Collectors.toSet());
        return RoomResponse.builder()
                .roomId(room.getRoomId())
                .roomType(room.getRoomType())
                .hex(room.getHex())
                .title(room.getTitle())
                .description(room.getDescription())
                .image(room.getImage())
                .textUrl3D(room.getTextUrl3D())
                .collections(collections)
                .build();
    }
}
