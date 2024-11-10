package com.dcode.order_service.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse user;
    private MessageResponse lastMessage;

    @Data
    @AllArgsConstructor
    public static class UserResponse {
        private String id;
        private String fullName;
        private String phoneNumber;
        private String email;
    }
}
