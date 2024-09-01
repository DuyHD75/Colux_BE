package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Paint;
import com.dcode.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaintRepository extends JpaRepository<Paint, Long> {
    Optional<Paint> findByPaintId(String paintId);
}
