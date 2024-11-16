package com.dcode.order_service.repository;

import com.dcode.order_service.entity.order.OrderEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {


/*    @Query("SELECT o FROM OrderEntity o WHERE o.user.username = :username")
    Page<OrderEntity> findAllByUsername(@Param("username") String username, Pageable pageable);*/

    Optional<OrderEntity> findByCode(String code);

    Optional<OrderEntity> findByPaypalOrderId(String paypalOrderId);

    List<OrderEntity> findByCustomerId(String customerId);

    List<OrderEntity> findAllByPaypalOrderStatus (String paypalStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OrderEntity> findByOrderId(@Param("orderId") String orderId);

    @Query("SELECT o FROM OrderEntity o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.paymentStatus IN :paymentStatuses AND o.status IN :statuses")
    List<OrderEntity> findByDateRangeAndStatusAndPaymentStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("paymentStatuses") List<Integer> paymentStatuses,
            @Param("statuses") List<Integer> statuses
    );

    @Query("SELECT o.createdAt, o.totalPay " +
            "FROM OrderEntity o " +
            "WHERE o.paymentStatus = 2 AND o.createdAt >= :startDate")
    List<Object[]> getRevenueForOrders(@Param("startDate") LocalDateTime startDate);




}
