package com.dcode.order_service.dto.waybill.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GhnDetailOrderRequest {

    @JsonProperty("order_code")
    private String orderCode;

    public GhnDetailOrderRequest(String code) {
        this.orderCode = code;
    }
}
