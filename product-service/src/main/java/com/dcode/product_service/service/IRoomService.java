package com.dcode.product_service.service;

import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.dtoResponse.RoomResponse;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRoomService {
    void createARoom(String roomType, String hex, String title, String description, String image, String textUrl3D);

    RoomResponse getARoom(String roomId);
    List<RoomResponse> getAllRoom();

    PageResponse<ColorResponse> getColorByRoom(String roomId, Pageable pageable);
}
