package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByCode(String brandCode);
    Optional<Brand> findBrandByBrandId(String brandId);

}
