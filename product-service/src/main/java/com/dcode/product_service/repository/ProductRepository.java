package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.lang.Double;

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


  @Query("SELECT DISTINCT p FROM Product p " +
       "LEFT JOIN p.paints paint " +
       "LEFT JOIN paint.paintVariants paintVariant " +
       "LEFT JOIN p.wallpapers wallpaper " +
       "LEFT JOIN wallpaper.wallpaperVariants wallpaperVariant " +
       "LEFT JOIN p.floors floor " +
       "LEFT JOIN floor.floorVariants floorVariant " +
       "WHERE (:type IS NULL OR p.category.name = :type) " +
       "AND (:minPrice IS NULL OR :maxPrice IS NULL OR " +
       "EXISTS (SELECT 1 FROM paint.paintVariants pv WHERE pv.price BETWEEN :minPrice AND :maxPrice) OR " +
       "EXISTS (SELECT 1 FROM wallpaper.wallpaperVariants wv WHERE wv.price BETWEEN :minPrice AND :maxPrice) OR " +
       "EXISTS (SELECT 1 FROM floor.floorVariants fv WHERE fv.price BETWEEN :minPrice AND :maxPrice)) " +
       "AND (:properties IS NULL OR :propertyCount = (SELECT COUNT(DISTINCT pv.property.propertyId) FROM p.propertyValues pv WHERE pv.property.propertyId IN :properties AND pv.property.propertyId IS NOT NULL)) " +
       "AND (:features IS NULL OR :featureCount = (SELECT COUNT(DISTINCT fv.feature.featureId) FROM p.featureValues fv WHERE fv.feature.featureId IN :features AND fv.feature.featureId IS NOT NULL))")
Page<Product> filterProductsNative(@Param("type") String type,
                                   @Param("minPrice") Double minPrice,
                                   @Param("maxPrice") Double maxPrice,
                                   @Param("properties") List<String> properties,
                                   @Param("features") List<String> features,
                                   @Param("propertyCount") long propertyCount,
                                   @Param("featureCount") long featureCount,
                                   Pageable pageable);




}




