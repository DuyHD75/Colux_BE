package com.dcode.order_service.utils;

import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.OrderLineResponse;
import com.dcode.order_service.entity.order.OrderLineEntity;
import com.dcode.order_service.entity.order.OrderEntity;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class OrderUtils {

    public static OrderEntity createNewOrderEntity(OrderRequest request) {
        return OrderEntity.builder()
                .orderId(UUID.randomUUID().toString())
                .code(RandomString.make(12).toUpperCase())
                .status(0)
                .build();
    }

    public static Order fromOrderEntity(OrderEntity orderEntity) {
        Order order = new Order();
        BeanUtils.copyProperties(orderEntity, order);
        order.setCode(orderEntity.getCode());
        return null;
    }


    public static OrderLineEntity createNewOrderLineEntity(OrderLineRequest request) {
        return OrderLineEntity.builder()
                .orderLineId(UUID.randomUUID().toString())
                .productId(request.productId())
                .orderEntity(OrderEntity.builder().orderId(request.orderId()).build())
                .quantity(request.quantity())
                .build();
    }


    public static OrderLineResponse fromOrderLineEntity(OrderLineEntity orderLine) {
        return new OrderLineResponse(
                orderLine.getId(),
                orderLine.getProductId(),
                orderLine.getQuantity()
        );
    }


}
