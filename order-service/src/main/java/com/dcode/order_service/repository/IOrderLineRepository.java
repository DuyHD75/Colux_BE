package com.dcode.order_service.repository;

import com.dcode.order_service.entity.order.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderLineRepository extends JpaRepository<OrderLineEntity, Long> {
    List<OrderLineEntity> findByOrderEntity_orderId (String orderId);
    List<OrderLineEntity> findAllByOrderEntityId (Long orderId);
    boolean existsByOrderEntity_customerIdAndProductId(String customerId, String productId);
}
