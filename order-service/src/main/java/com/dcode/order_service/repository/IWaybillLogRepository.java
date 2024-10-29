package com.dcode.order_service.repository;

import com.dcode.order_service.entity.waybill.WaybillLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IWaybillLogRepository extends JpaRepository<WaybillLog, Long> {
//    List<WaybillLog> findByWaybillId(String waybillId);
}
