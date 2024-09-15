package com.dcode.product_service.service.impl;

import com.dcode.product_service.entity.Room;
import com.dcode.product_service.repository.RoomRepository;
import com.dcode.product_service.service.IRoomService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dcode.product_service.utils.RoomUtils.createNewRoomEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class RoomServiceImpl implements IRoomService {

    private final RoomRepository roomRepository;

    @Override
    public void createARoom(String roomType, String image, String textUrl3D) {
        roomRepository.save(createARoomEntity(roomType, image, textUrl3D));
    }

    private Room createARoomEntity(String roomType, String image, String textUrl3D) {
        return createNewRoomEntity(roomType, image, textUrl3D);
    }
}
