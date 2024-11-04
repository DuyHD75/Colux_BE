package com.dcode.order_service.service;

import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.entity.order.OrderEntity;

import java.util.List;
import java.util.Map;

public interface IEmailService {
    void sendOrderPlacedEmail(OrderEntity orderEntity, List<CartVariantResponse.ClientVariantResponse> productLines, Map<?, ?> data);
    void sendOrderCancelledEmail(OrderEntity orderEntity, List<CartVariantResponse.ClientVariantResponse> productLines, Map<?, ?> data);
    void sendOrderCompletedEmail(OrderEntity orderEntity, List<CartVariantResponse.ClientVariantResponse> productLines, Map<?, ?> data);
}
