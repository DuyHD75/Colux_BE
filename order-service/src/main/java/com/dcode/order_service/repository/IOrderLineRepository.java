package com.dcode.order_service.repository;

import com.dcode.order_service.entity.order.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderLineRepository extends JpaRepository<OrderLineEntity, Long> {
    List<OrderLineEntity> findAllByOrderEntityId (Long orderId);
}
