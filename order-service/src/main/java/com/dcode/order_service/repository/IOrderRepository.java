package com.dcode.order_service.repository;

import com.dcode.order_service.entity.order.OrderEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {


/*    @Query("SELECT o FROM OrderEntity o WHERE o.user.username = :username")
    Page<OrderEntity> findAllByUsername(@Param("username") String username, Pageable pageable);*/

    Optional<OrderEntity> findByCode(String code);

    Optional<OrderEntity> findByPaypalOrderId(String paypalOrderId);

//    Optional<OrderEntity> findByOrderId(String orderId);

    List<OrderEntity> findAllByPaypalOrderStatus (String paypalStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OrderEntity> findByOrderId(@Param("orderId") String orderId);


}
