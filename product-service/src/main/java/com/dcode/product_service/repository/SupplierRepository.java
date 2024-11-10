package com.dcode.product_service.repository;

import com.dcode.product_service.entity.ProductSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<ProductSupplier, Long> {
}
