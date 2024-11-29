package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Floor;
import com.dcode.product_service.entity.FloorVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FloorVariantRepository extends JpaRepository<FloorVariant, Long> {
    Optional<FloorVariant> findByFloor_Product_productIdAndFloor_numberOfPiecesPerBoxAndVariant_sizeNameAndVariant_categoryName(String productId, Integer numberOfPiecesPerBox, String sizeName, String categoryName);
    Optional<FloorVariant> findByFloor_floorIdAndVariant_VariantId(String floorId, String variantId);

    void deleteByFloor (Floor floor);
}
