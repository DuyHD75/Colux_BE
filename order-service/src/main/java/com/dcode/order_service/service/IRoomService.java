package com.dcode.order_service.service;

import com.dcode.order_service.dto.chat.request.RoomRequest;
import com.dcode.order_service.dto.chat.response.ClientRoomExistenceResponse;
import com.dcode.order_service.dto.chat.response.MessageResponse;
import com.dcode.order_service.dto.chat.response.RoomResponse;

import java.util.List;

public interface IRoomService {

    RoomResponse createRoom(RoomRequest roomRequest);

    ClientRoomExistenceResponse getRoom(String roomId);

}
