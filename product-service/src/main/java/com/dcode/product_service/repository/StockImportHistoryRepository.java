package com.dcode.product_service.repository;

import com.dcode.product_service.entity.StockImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockImportHistoryRepository extends JpaRepository<StockImportHistory, Long> {
}
