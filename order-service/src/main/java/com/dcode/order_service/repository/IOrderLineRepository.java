package com.dcode.order_service.repository;

import com.dcode.order_service.entity.order.OrderLineEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface IOrderLineRepository extends JpaRepository<OrderLineEntity, Long> {
    List<OrderLineEntity> findByOrderEntity_orderId (String orderId);
    List<OrderLineEntity> findAllByOrderEntityId (Long orderId);
    boolean existsByOrderEntity_customerIdAndProductId(String customerId, String productId);

    @Query("SELECT ol.productId AS productId, COUNT(ol.productId) AS occurrences, SUM(ol.quantity) AS totalQuantity " +
            "FROM OrderLineEntity ol " +
            "GROUP BY ol.productId " +
            "ORDER BY occurrences DESC")
    List<Map<String, Object>> findTop5MostFrequentProductsWithQuantity(Pageable pageable);

}
