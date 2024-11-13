package com.dcode.order_service.service.impl;

import com.dcode.order_service.dto.chat.request.RoomRequest;
import com.dcode.order_service.dto.chat.response.ClientRoomExistenceResponse;
import com.dcode.order_service.dto.chat.response.RoomResponse;
import com.dcode.order_service.entity.chat.Message;
import com.dcode.order_service.entity.chat.Room;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.proxy.ICustomerClientProxy;
import com.dcode.order_service.repository.IMessageRepository;
import com.dcode.order_service.repository.IRoomRepository;
import com.dcode.order_service.service.IRoomService;
import com.dcode.order_service.utils.MessageUtils;
import com.dcode.order_service.utils.RoomUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoomServiceImpl implements IRoomService {

    private final ICustomerClientProxy clientProxy;
    private final IRoomRepository roomRepository;
    private final IMessageRepository messageRepository;
    private final RoomUtils roomUtils;
    private final MessageUtils messageMapper;


    @Override
    public RoomResponse createRoom(RoomRequest roomRequest) {
        // (1) check the room of the user if it exists or not
        if (checkRoomExistence(roomRequest) != null) {
            return checkRoomExistence(roomRequest);
        }

        // (2) create a new room
        Room room = new Room();
        String userId = roomRequest.getUserId();

        // (2.1) Anonymous user with the phone and email when start chat
        if (userId == null) {
            room.setFullName("Anonymous");
            room.setPhoneNumber(roomRequest.getPhoneNumber());
            room.setEmail(roomRequest.getEmail());
        } else {
            // (2.2) With logged in user

            var userFetch = clientProxy.findUserByUserId(userId)
                    .orElseThrow(() -> new BusinessException("Cannot create room chat :: No customer found with ID: " + userId));

            Map<String, Object> userMap = (Map<String, Object>) userFetch.data().get("user");
            room.setUserId(userMap.get("userId").toString());
            room.setFullName(userMap.get("firstName").toString() + " " + userMap.get("lastName").toString());
            room.setEmail(userMap.get("email").toString());
            room.setPhoneNumber(
                    userMap.get("phoneNumber") != null ? userMap.get("phoneNumber").toString() : roomRequest.getPhoneNumber()
            );

            room.setEmail(userMap.get("email").toString());
        }
        room.setRoomId(UUID.randomUUID().toString());
        Room roomAfterSave = roomRepository.save(room);

        return roomUtils.entityToResponse(roomAfterSave);
    }


    @Override
    public ClientRoomExistenceResponse getRoom(String roomId) {
        RoomResponse roomResponse = roomRepository.findByRoomId(roomId)
                .map(roomUtils::entityToResponse)
                .orElse(null);

        var clientRoomExistenceResponse = new ClientRoomExistenceResponse();
        clientRoomExistenceResponse.setRoomExistence(roomResponse != null);
        clientRoomExistenceResponse.setRoomResponse(roomResponse);
        clientRoomExistenceResponse.setRoomRecentMessages(
                roomResponse != null
                        ? messageMapper.entityToResponse(
                        messageRepository
                                .findByRoomId(
                                        roomResponse.getId(),
                                        PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id")))
                                .stream()
                                .sorted(Comparator.comparing(Message::getId))
                                .collect(Collectors.toList()))
                        : Collections.emptyList());

        return clientRoomExistenceResponse;
    }

    @Override
    public List<ClientRoomExistenceResponse> getAllRooms() {


        List<RoomResponse> roomResponses = roomRepository.findAll()
                .stream()
                .map(roomUtils::entityToResponse)
                .collect(Collectors.toList());

        if (roomResponses.size() > 0) {
            return roomResponses.stream()
                    .map(roomResponse -> {
                        var clientRoomExistenceResponse = new ClientRoomExistenceResponse();
                        clientRoomExistenceResponse.setRoomExistence(true);
                        clientRoomExistenceResponse.setRoomResponse(roomResponse);

                        return clientRoomExistenceResponse;
                    })
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private RoomResponse checkRoomExistence(RoomRequest roomRequest) {
        Optional<Room> existingRoom = Optional.empty();

        if (roomRequest.getUserId() != null) {
            existingRoom = roomRepository.findByUserId(roomRequest.getUserId());
        } else if (roomRequest.getEmail() != null) {
            existingRoom = roomRepository.findByEmail(roomRequest.getEmail());
        } else if (roomRequest.getPhoneNumber() != null) {
            existingRoom = roomRepository.findByPhoneNumber(roomRequest.getPhoneNumber());
        }

        if (existingRoom.isPresent()) {
            return roomUtils.entityToResponse(existingRoom.get());
        }

        return null;
    }

}

