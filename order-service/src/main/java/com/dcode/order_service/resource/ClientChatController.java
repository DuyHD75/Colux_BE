package com.dcode.order_service.resource;

import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.chat.request.RoomRequest;
import com.dcode.order_service.dto.chat.response.ClientRoomExistenceResponse;
import com.dcode.order_service.dto.chat.response.RoomResponse;
import com.dcode.order_service.entity.chat.Message;
import com.dcode.order_service.repository.IMessageRepository;
import com.dcode.order_service.repository.IRoomRepository;
import com.dcode.order_service.service.IRoomService;
import com.dcode.order_service.utils.MessageUtils;
import com.dcode.order_service.utils.RoomUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dcode.order_service.utils.RequestUtils.getResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/chats")
public class ClientChatController {

    private final IRoomRepository roomRepository;
    private final IMessageRepository messageRepository;
    private final IRoomService roomService;


    private final MessageUtils messageMapper;
    private final RoomUtils roomUtils;

    @GetMapping("/test")
    public String getRoom() {
        return "Hello World!";
    }

    @PostMapping("/create-room")
    public ResponseEntity<Response> createRoom(@RequestBody RoomRequest roomRequest, HttpServletRequest request, HttpServletResponse response) {
        var roomResponse = roomService.createRoom(roomRequest);
        return ResponseEntity.created(getUri()).body(
                getResponse(request, "Room created successfully!", CREATED, Map.of("dataRoom", roomResponse))
        );
    }

    @GetMapping("/get-room/{roomId}")
    public ResponseEntity<Response> getRoom(@PathVariable String roomId, HttpServletRequest request, HttpServletResponse response) {
        var clientRoomExistenceResponse = roomService.getRoom(roomId);
        return ResponseEntity.status(OK).body(
                getResponse(request, "Room found!", OK, Map.of("dataRoom", clientRoomExistenceResponse))
        );
    }

    private URI getUri() {
        return URI.create("/client-api/chat");
    }

}
