package com.dcode.order_service.utils;

import com.dcode.order_service.dto.chat.request.MessageRequest;
import com.dcode.order_service.dto.chat.request.RoomRequest;
import com.dcode.order_service.dto.chat.response.MessageResponse;
import com.dcode.order_service.dto.chat.response.RoomResponse;
import com.dcode.order_service.entity.chat.Message;
import com.dcode.order_service.entity.chat.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RoomUtils implements GenericMapper<Room, RoomRequest, RoomResponse> {

    private final MessageUtils messageUtils;

    @Override
    public Room requestToEntity(RoomRequest request) {
        return Room.builder()
                .userId(request.getUserId())
                .build();
    }

    @Override
    public RoomResponse entityToResponse(Room entity) {
        RoomResponse roomResponse = new RoomResponse(
                entity.getId(), entity.getRoomId(), entity.getCreatedAt(), entity.getUpdatedAt(),
                new RoomResponse.UserResponse(entity.getUserId(), entity.getFullName(), entity.getPhoneNumber(), entity.getEmail()),
                messageUtils.entityToResponse(entity.getLastMessage())
        );
        return roomResponse;
    }


    @Override
    public List<RoomResponse> entityToResponse(List<Room> entities) {
        return List.of();
    }

    @Override
    public Room partialUpdate(Room entity, RoomRequest request) {
        return null;
    }
}
