package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Paint;
import com.dcode.product_service.entity.PaintVariant;
import com.dcode.product_service.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaintVariantRepository extends JpaRepository<PaintVariant, Long> {
    Optional<PaintVariant> findByPaintAndVariant(Paint paint, Variant variant);
    void deleteByPaint(Paint paint);
}
