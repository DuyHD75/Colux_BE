package com.dcode.order_service.dto.chat.request;

import lombok.Data;

@Data
public class RoomRequest {
    private String name;
    private String userId;
    private String phoneNumber;
    private String email;
}
