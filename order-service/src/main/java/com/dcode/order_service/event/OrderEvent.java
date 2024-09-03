package com.dcode.order_service.event.listener;

import com.dcode.order_service.enumuration.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class OrderEvent {
    private String orderId;
    private EventType eventType;
    private Map<? , ?> data;
}
