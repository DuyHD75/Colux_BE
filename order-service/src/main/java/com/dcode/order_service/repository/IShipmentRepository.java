package com.dcode.order_service.repository;

import com.dcode.order_service.entity.order.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IShipmentRepository extends JpaRepository<ShipmentEntity, Long> {
    List<ShipmentEntity> findByCustomerId(String customerId);
    Optional<ShipmentEntity> findByShipmentId(String shipmentId);
    Optional<ShipmentEntity> findByCustomerIdAndShipmentId(String customerId, String shipmentId);
    void deleteByCustomerIdAndShipmentId(String customerId, String shipmentId);
}
