package com.dcode.order_service.dto.waybill.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WaybillLogResponse {
    private String waybillLogId;
    private String previousStatus;
    private String currentStatus;
    private LocalDateTime createdAt;
}
