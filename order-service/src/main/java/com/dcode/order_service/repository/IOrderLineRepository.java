package com.dcode.order_service.repository;

import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.entity.order.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderLineRepository extends JpaRepository<OrderLineEntity, Long> {
    List<OrderLineEntity> findAllByOrderId(String orderId);

    void saveOrderLine(OrderLineRequest orderLineRequest);
}
