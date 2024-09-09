package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Floor;
import com.dcode.product_service.entity.FloorVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FloorVariantRepository extends JpaRepository<FloorVariant, Long> {
    void deleteByFloor (Floor floor);
}
