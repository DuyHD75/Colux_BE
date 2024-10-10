package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.RoomRequest;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.dtoResponse.RoomResponse;
import com.dcode.product_service.entity.Color;
import com.dcode.product_service.entity.PageResponse;
import com.dcode.product_service.entity.PageResponseBuilder;
import com.dcode.product_service.entity.Room;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.ColorRepository;
import com.dcode.product_service.repository.RoomRepository;
import com.dcode.product_service.service.IRoomService;
import com.dcode.product_service.utils.ColorUtils;
import com.dcode.product_service.utils.RoomUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dcode.product_service.utils.RoomUtils.*;

@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class RoomServiceImpl implements IRoomService {

    private final RoomRepository roomRepository;
    private final ColorRepository colorRepository;

    @Override
    public void createRooms(List<RoomRequest> roomRequests) {
        roomRequests.forEach(roomRequest -> {
            Room room = createARoomEntity(roomRequest);
            roomRepository.save(room);
        });
    }

    @Override
    public RoomResponse getARoom(String roomId) {
        var roomEntity = roomRepository.findByRoomId(roomId).orElseThrow(()-> new ApiException("Room retrieve successfully!"));
        return fromRoomEntity(roomEntity);

    }

    @Override
    public List<RoomResponse> getAllRoom() {
        var rooms = roomRepository.findAll();
        return rooms.stream()
                .map(RoomUtils::fromRoomEntity)
                .toList();
    }

    @Override
    public PageResponse<ColorResponse> getColorByRoom(String roomId, Pageable pageable) {
        var colors = colorRepository.findByCollections_Room_RoomId(roomId, pageable);
        Page<ColorResponse> colorResponsePage = colors.map(ColorUtils::fromColorEntityPartical);
        return PageResponseBuilder.buildPageResponse(colorResponsePage);
    }

    private Room createARoomEntity(RoomRequest roomRequest) {
        return createNewRoomEntity(roomRequest);
    }
}
