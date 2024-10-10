package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.RoomRequest;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.dtoResponse.RoomResponse;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRoomService {
    void createRooms(List<RoomRequest> roomRequests);

    RoomResponse getARoom(String roomId);
    List<RoomResponse> getAllRoom();

    PageResponse<ColorResponse> getColorByRoom(String roomId, Pageable pageable);
}
