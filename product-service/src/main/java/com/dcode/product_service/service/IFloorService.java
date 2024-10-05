package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.FloorRequest;
import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.FloorResponse;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface IFloorService {
    void createAFloor(String productId, FloorRequest floorRequest);

    FloorResponse getAFloor(String floorId);

    void updateAFloor(String floorId, Double foamThickness, String accessoryType, String packagingMaterial, Integer numberOfPiecesPerBox, Set<VariantRequest> variants);

    void deleteAFloor(String floorId);

    PageResponse<FloorResponse> getAllFloorPageable(Pageable pageable);
}
