package com.dcode.order_service.dto.chat.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageRequest {
    private String sender;
    private String content;
    private Integer status;
    private String userId;
    private String roomId;
}
