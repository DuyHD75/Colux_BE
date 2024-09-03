package com.dcode.order_service.service;

import com.dcode.order_service.dto.order.response.OrderLineResponse;

import java.util.List;

public interface IOrderLineService {
    List<OrderLineResponse> findAllByOrderId(String orderId);
}
