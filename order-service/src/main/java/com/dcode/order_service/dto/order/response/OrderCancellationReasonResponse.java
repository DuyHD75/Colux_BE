package com.dcode.order_service.dto.order.response;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class OrderCancellationReasonResponse {
    private Long id;
    private String createdAt;
    private String updatedAt;
    private String name;
    @Nullable
    private String note;
    private Integer status;
}
