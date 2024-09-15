package com.dcode.product_service.utils;

import com.dcode.product_service.entity.Room;

import java.util.UUID;

public class RoomUtils {
    public static Room createNewRoomEntity(String roomType, String image, String textUrl3D){
        return Room.builder()
                .roomId(UUID.randomUUID().toString())
                .roomType(roomType)
                .image(image)
                .textUrl3D(textUrl3D)
                .build();
    }
}
