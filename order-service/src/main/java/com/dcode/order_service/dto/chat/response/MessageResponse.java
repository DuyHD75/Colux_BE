package com.dcode.order_service.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String content;
    private Integer status;
    private UserResponse user;

    @Data
    @AllArgsConstructor
    public static class UserResponse {
        private String userId;
        private String fullName;
        private String phoneNumber;
        private String email;
    }
}

