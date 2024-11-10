package com.dcode.order_service.repository;

import com.dcode.order_service.entity.waybill.Waybill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IWaybillRepository extends JpaRepository<Waybill, Long> {
    Optional<Waybill> findByWaybillId (String waybillId);
    Optional<Waybill> findByOrder_OrderId (String orderId);
    Optional<Waybill> findByCode (String code);

    List<Waybill> findAllByStatusIn(List<Integer> statuses);
    long countByStatusIn(List<Integer> statuses);
}
