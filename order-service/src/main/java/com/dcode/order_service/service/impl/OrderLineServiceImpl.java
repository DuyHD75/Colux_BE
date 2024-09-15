package com.dcode.order_service.service.impl;

import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.dto.order.response.OrderLineResponse;
import com.dcode.order_service.repository.IOrderLineRepository;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.service.IOrderLineService;
import com.dcode.order_service.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderLineServiceImpl implements IOrderLineService {

    private final IOrderLineRepository orderRepository;

    @Override
    public void saveOrderLine(OrderLineRequest request) {
        var order = OrderUtils.toOrderLineEntity(request);
        orderRepository.save(order);
    }

    @Override
    public List<OrderLineResponse> findAllByOrderId(Long orderId) {
        return  orderRepository.findAllByOrderEntityId(orderId).stream()
                .map(OrderUtils::fromOrderLineEntity)
                .toList();
    }
}
