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
}
