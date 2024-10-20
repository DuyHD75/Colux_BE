package com.dcode.order_service.service;


import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.OrderResponse;

import java.util.List;

public interface IOrderService {
    void cancelOrder(String code);

    String createClientOrder(OrderRequest request);

    void captureTransactionPaypal(String paypalOrderId, String payerId);

    List<Order> getAllOrders();

}
