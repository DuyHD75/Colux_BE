package com.dcode.order_service.dto.order.response;

import com.dcode.order_service.enumuration.PaymentMethod;
import lombok.Data;

@Data
public class ConfirmedOrderResponse {

    private String orderCode;
    private PaymentMethod message;
    private String orderPaypalCheckoutLink;
}