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
        // if the request have the toname field, we can use it to create the order if not we can use the customer name
        return OrderEntity.builder()
                .orderId(UUID.randomUUID().toString())
                .code(RandomString.make(12).toUpperCase())
                .status(1)
                .toName("Nguyen Van Hoang")
                .toPhone("090567665")
                .toAddress("123/4/5")
                .toWardName("Phuong 1")
                .toDistrictName("Quan 1")
                .toProvinceName("TP HCM")
                .customerId(request.getCustomerId())
                .totalAmount(request.getTotalAmount())
                .tax(request.getTax())
                .shippingCost(request.getShippingCost())
                .totalPay(request.getTotalPay())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .note(request.getNote())
                .build();
    }

    public static Order fromOrderEntity(OrderEntity orderEntity) {
        Order order = new Order();
        BeanUtils.copyProperties(orderEntity, order);
        order.setCode(orderEntity.getCode());
        return null;
    }


    public static OrderLineEntity toOrderLineEntity(OrderLineRequest request) {
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
                orderLine.getVariantId(),
                orderLine.getColorId(),
                orderLine.getQuantity()
        );
    }


}
