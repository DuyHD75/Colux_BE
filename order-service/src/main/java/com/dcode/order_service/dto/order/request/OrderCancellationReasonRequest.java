package com.dcode.order_service.dto.order.request;

import org.springframework.lang.Nullable;

import lombok.Data;

@Data
public class OrderCancellationReasonRequest {
    private String name;
    @Nullable
    private String note;
    private Integer status;
}
