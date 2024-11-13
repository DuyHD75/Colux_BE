package com.dcode.product_service.repository;

import com.dcode.product_service.entity.ProductSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<ProductSupplier, Long> {
    Optional<ProductSupplier> findBySupplierId(String supplierId);
}
