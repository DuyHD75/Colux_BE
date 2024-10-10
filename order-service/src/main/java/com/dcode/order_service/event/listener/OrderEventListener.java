package com.dcode.order_service.event.listener;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;

@AllArgsConstructor
public class OrderEventListener {

    @EventListener
    public void onOrderEvent(OrderEvent event) {
        switch (event.getEventType()) {
            case ORDER_CREATED ->
                    System.out.println("Order Placed");
            case ORDER_CANCELLED ->
                    System.out.println("Order Cancelled");
            case ORDER_COMPLETED ->
                    System.out.println("Order Completed");
            default -> {
            }
        }
    }


}
