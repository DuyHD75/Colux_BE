package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Category;
import com.dcode.product_service.entity.Paint;
import com.dcode.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product> {

  Optional<Product> findByProductId(String productId);
  @Query("SELECT p FROM Product p " +
          "LEFT JOIN p.paints pa " +
          "LEFT JOIN p.wallpapers w " +
          "LEFT JOIN p.floors f " +
          "WHERE p.category.categoryId = :categoryId " +
          "AND (pa IS NOT NULL OR w IS NOT NULL OR f IS NOT NULL)")
  Page<Product> findAllByCategory_categoryIdAndNonNullFields(@Param("categoryId") String categoryId, Pageable pageable);

  List<Product> findAllByProductIdIn (List<String> productIds);

  @Query("SELECT p FROM Product p " +
          "LEFT JOIN p.paints pa " +
          "LEFT JOIN p.wallpapers w " +
          "LEFT JOIN p.floors f " +
          "WHERE (pa IS NOT NULL OR w IS NOT NULL OR f IS NOT NULL)")
  Page<Product> findProductsWithNonNullPaintWallpaperFloor(Pageable pageable);


  @Query(value = "SELECT p.* " +
          "FROM products p " +
          "LEFT JOIN brands b ON p.brand_id = b.id " +
          "LEFT JOIN paints pa ON p.id = pa.product_id " +
          "LEFT JOIN colors c ON pa.color_id = c.id " +
          "WHERE (:keyword IS NULL OR " +
          "LOWER(p.product_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "LOWER(c.hex) LIKE LOWER(CONCAT('%', :keyword, '%')))",
          nativeQuery = true)
  List<Product> searchProductsByKeyword(@Param("keyword") String keyword);


  @Query(value = """
    SELECT DISTINCT p.*
    FROM products p
    LEFT JOIN paints pt ON p.id = pt.product_id
    LEFT JOIN paint_variant pv ON pt.id = pv.paint_id
    LEFT JOIN wallpapers wp ON p.id = wp.product_id
    LEFT JOIN wallpaper_variant wv ON wp.id = wv.wallpaper_id
    LEFT JOIN floors fl ON p.id = fl.product_id
    LEFT JOIN floor_variant fv ON fl.id = fv.floor_id
    LEFT JOIN product_feature_value pfv ON p.id = pfv.product_id
    LEFT JOIN feature_values fv2 ON pfv.feature_value_id = fv2.id
    LEFT JOIN features f ON fv2.feature_id = f.id
    LEFT JOIN product_property_value ppv ON p.id = ppv.product_id
    LEFT JOIN property_values pv2 ON ppv.property_value_id = pv2.id
    LEFT JOIN properties pr ON pv2.property_id = pr.id
    WHERE
        (:type IS NULL OR
            (:type = 'paint' AND pt.id IS NOT NULL) OR
            (:type = 'wallpaper' AND wp.id IS NOT NULL) OR
            (:type = 'floor' AND fl.id IS NOT NULL)
        )
        AND (
            (:type = 'paint' AND (:minPrice IS NULL OR pv.price >= :minPrice) AND (:maxPrice IS NULL OR pv.price <= :maxPrice)) OR
            (:type = 'wallpaper' AND (:minPrice IS NULL OR wv.price >= :minPrice) AND (:maxPrice IS NULL OR wv.price <= :maxPrice)) OR
            (:type = 'floor' AND (:minPrice IS NULL OR fv.price >= :minPrice) AND (:maxPrice IS NULL OR fv.price <= :maxPrice))
        )
        -- Lọc theo features (sản phẩm phải có tất cả các feature được yêu cầu)
        AND NOT EXISTS (
            SELECT 1
            FROM features f_sub
            WHERE f_sub.id IN (:features)
              AND NOT EXISTS (
                SELECT 1
                FROM product_feature_value pfv_sub
                         JOIN feature_values fv_sub ON pfv_sub.feature_value_id = fv_sub.id
                WHERE pfv_sub.product_id = p.id AND fv_sub.feature_id = f_sub.id
            )
        )
        -- Lọc theo properties (sản phẩm phải có tất cả các property value của các propertyId yêu cầu)
        AND NOT EXISTS (
            SELECT 1
            FROM properties pr_sub
            WHERE pr_sub.id IN (:properties)
            AND NOT EXISTS (
                -- Kiểm tra xem sản phẩm có bất kỳ property value nào của propertyId không
                SELECT 1
                FROM product_property_value ppv_sub
                JOIN property_values pv_sub ON ppv_sub.property_value_id = pv_sub.id
                WHERE ppv_sub.product_id = p.id 
                AND pv_sub.property_id = pr_sub.id
                AND pr_sub.id IN (:properties)  -- Lọc các property theo propertyId truyền vào
            )
        )
        -- Kiểm tra nếu sản phẩm có tất cả các property value của các propertyId
        GROUP BY p.id
        HAVING COUNT(DISTINCT pr.id) = :propertiesCount
    """, nativeQuery = true)
  Page<Product> filterProductsNative(
          @Param("type") String type,
          @Param("minPrice") Double minPrice,
          @Param("maxPrice") Double maxPrice,
          @Param("features") List<String> features,
          @Param("properties") List<String> properties,
          @Param("propertiesCount") Long propertiesCount,
                  Pageable pageable
  );





}
