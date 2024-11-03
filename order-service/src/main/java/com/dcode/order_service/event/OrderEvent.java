package com.dcode.order_service.event;

import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.enumuration.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class OrderEvent {
    private OrderEntity orderEntity;
    private EventType eventType;
    private List<CartVariantResponse.ClientVariantResponse> productLines;
    private Map<? , ?> data;
}
