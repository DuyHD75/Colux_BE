package com.dcode.order_service.service.impl;

import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.dto.order.response.OrderLineResponse;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.repository.IOrderLineRepository;
import com.dcode.order_service.service.IOrderLineService;
import com.dcode.order_service.utils.OrderUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderLineServiceImpl implements IOrderLineService {

    private final EntityManager entityManager;

    private final IOrderLineRepository orderLineRepository;

    @Override
    public void saveOrderLine(OrderLineRequest request) {
        var orderLineEntity = OrderUtils.toOrderLineEntity(request);
        OrderEntity orderEntity = Optional.ofNullable(request.orderId())
                        .map(id -> entityManager.unwrap(Session.class)
                                .byNaturalId(OrderEntity.class)
                                        .using("orderId", id)
                                        .getReference())
                                .orElse(null);
        orderLineEntity.setOrderEntity(orderEntity);
        orderLineRepository.save(orderLineEntity);
    }

    @Override
    public List<OrderLineResponse> findAllByOrderId(Long orderId) {
        return  orderLineRepository.findAllByOrderEntityId(orderId).stream()
                .map(OrderUtils::fromOrderLineEntity)
                .toList();
    }
}
