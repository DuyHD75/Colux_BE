package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long> {
    Optional<Floor> findByProduct_ProductIdAndNumberOfPiecesPerBox(String productProductId, Integer numberOfPiecesPerBox);
    Optional<Floor> findByFloorId(String floorId);
    Optional<Floor> findByFloorIdAndStatus(String floorId, Integer status);

}
