    package com.dcode.product_service.repository;

    import com.dcode.product_service.entity.Paint;
    import com.dcode.product_service.entity.PaintVariant;
    import com.dcode.product_service.entity.Variant;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.Optional;

    public interface PaintVariantRepository extends JpaRepository<PaintVariant, Long> {
        Optional<PaintVariant> findByPaint_paintIdAndVariant_variantId(String paintId, String variantId);
        Optional<PaintVariant> findByPaint_paintIdAndVariant_sizeName(String paintId, String sizeName);
        Optional<PaintVariant> findByPaint_Product_productIdAndPaint_Color_hexAndVariant_sizeNameAndVariant_categoryName(String productId, String hex, String sizeName, String categoryName);
        void deleteByPaint(Paint paint);
    }
