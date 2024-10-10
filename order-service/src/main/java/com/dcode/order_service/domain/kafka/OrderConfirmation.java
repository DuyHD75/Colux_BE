package com.dcode.order_service.domain.kafka;

import com.dcode.order_service.dto.user.UserResponse;
import com.dcode.order_service.dto.product.PurchaseResponse;
import com.dcode.order_service.enumuration.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        UserResponse customer,
        List<PurchaseResponse> products
) {
}
