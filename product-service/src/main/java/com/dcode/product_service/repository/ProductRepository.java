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
  Page<Product> findProductByCategory(Category category, Pageable pageable);
  List<Product> findAllByProductIdIn (List<String> productIds);

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



}
