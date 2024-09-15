package com.dcode.order_service.event.listener;

import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.enumuration.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class OrderEvent {
    private OrderEntity orderEntity;
    private EventType eventType;
    private Map<? , ?> data;
}
