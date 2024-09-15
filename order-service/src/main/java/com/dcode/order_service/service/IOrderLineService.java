package com.dcode.order_service.service;

import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.dto.order.response.OrderLineResponse;

import java.util.List;

public interface IOrderLineService {

    void saveOrderLine(OrderLineRequest request);

    List<OrderLineResponse> findAllByOrderId(Long orderId);


}
