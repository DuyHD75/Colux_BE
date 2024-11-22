package com.dcode.order_service.dto.order.request;

import org.springframework.lang.Nullable;

import lombok.Data;

@Data
public class OrderCancellationReasonRequest {
    private String code;
    private String cancelReason;
}
