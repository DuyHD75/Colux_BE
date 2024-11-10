package com.dcode.order_service.resource;

import com.dcode.order_service.dto.chat.ListResponse;
import com.dcode.order_service.dto.chat.request.MessageRequest;
import com.dcode.order_service.dto.chat.response.MessageResponse;
import com.dcode.order_service.exception.ResourceNotFoundException;
import com.dcode.order_service.service.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatResource {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final IMessageService messageService;

    @MessageMapping("/{roomId}")
    @SendTo("/chat/receive/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, @Payload MessageRequest message) throws ResourceNotFoundException {
        MessageResponse messageResponse = messageService.save(message);
        simpMessagingTemplate.convertAndSend("/chat/receive/" + roomId, messageResponse);
    }

    @MessageMapping("/addUser")
    @SendTo("/chat/receive/{roomId}")
    public void addUser(@Payload MessageRequest message, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("name", message.getSender());
        simpMessagingTemplate.convertAndSend("/chat/receive/" + message.getRoomId(), message);
    }

    @GetMapping("/messages")
    public ResponseEntity<ListResponse<MessageResponse>> getAllMessages(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id,desc") String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "all", required = false) boolean all
    ) {
        ListResponse<MessageResponse> messageResponses = messageService.findAll(page, size, sort, filter, search, all);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponses);
    }
}
