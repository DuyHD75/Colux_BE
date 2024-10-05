package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Paint;
import com.dcode.product_service.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface VariantRepository extends JpaRepository<Variant, Long> {
//    Optional<Variant> findBySizeNameAndPaint(String sizeName, Paint paint);
//    Set<Variant> findByPaintIsNotNullAndWallpaperIsNullAndFloorIsNull();
//    Set<Variant> findByWallpaperIsNotNullAndPaintIsNullAndFloorIsNull();
    Optional<Variant> findByVariantId(String variantId);
    Set<Variant> findAllByVariantIdIn(Set<String> variantIds);
}
