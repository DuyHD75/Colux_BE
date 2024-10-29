package com.dcode.order_service.dto.order.response;

import com.dcode.order_service.enumuration.PaymentMethod;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ConfirmedOrderResponse {
    private String orderCode;
    private PaymentMethod paymentMethod;
    @Nullable
    private String orderPaypalCheckoutLink;
}