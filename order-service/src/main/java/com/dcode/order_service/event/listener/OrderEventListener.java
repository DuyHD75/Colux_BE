package com.dcode.order_service.event.listener;

import com.dcode.order_service.event.OrderEvent;
import com.dcode.order_service.service.impl.EmailServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderEventListener {
    private final EmailServiceImpl emailServiceImpl;

    @EventListener
    public void onOrderEvent(OrderEvent event) {
        switch (event.getEventType()) {
            case ORDER_CREATED ->
                    emailServiceImpl.sendOrderPlacedEmail(event.getOrderEntity(), event.getProductLines(), event.getData());
            case ORDER_CANCELLED ->
                    emailServiceImpl.sendOrderCancelledEmail(event.getOrderEntity(), event.getProductLines(), event.getData());
            case ORDER_COMPLETED ->
                    emailServiceImpl.sendOrderCompletedEmail(event.getOrderEntity(), event.getProductLines(), event.getData());
            default -> {
            }
        }
    }


}
