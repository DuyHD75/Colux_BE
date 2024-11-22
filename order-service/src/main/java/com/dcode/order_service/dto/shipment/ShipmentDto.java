package com.dcode.order_service.dto.shipment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShipmentDto {
    @NotNull(message = "CustomerId is required")
    private String customerId;
    private String shipmentId;
    private String customerName;
    private String customerPhone;
    private String toAddress;
    private String toWardName;
    private String toDistrictName;
    private String toProvinceName;
    private Integer status;
}
