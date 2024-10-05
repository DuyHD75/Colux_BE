package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.FloorRequest;
import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.FloorResponse;
import com.dcode.product_service.entity.Floor;
import com.dcode.product_service.entity.PageResponse;
import com.dcode.product_service.entity.PageResponseBuilder;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.FloorRepository;
import com.dcode.product_service.repository.FloorVariantRepository;
import com.dcode.product_service.repository.ProductRepository;
import com.dcode.product_service.repository.VariantRepository;
import com.dcode.product_service.service.IFloorService;
import com.dcode.product_service.utils.FloorUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.dcode.product_service.utils.FloorUtils.createNewFloorEntity;
import static com.dcode.product_service.utils.FloorUtils.fromFloorEntity;
import static com.dcode.product_service.utils.PaintUtils.checkVariantRequestSet;
import static com.dcode.product_service.utils.PaintUtils.extractVariantIds;

@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
@Slf4j
public class FloorServiceImpl implements IFloorService {
    private final FloorRepository floorRepository;
    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final FloorVariantRepository floorVariantRepository;

    @Override
    public void createAFloor(String productId, FloorRequest floorRequest) {
        floorRepository.save(createAFloorEntity(productId, floorRequest));
    }

    @Override
    public FloorResponse getAFloor(String floorId) {
        var floorEntity = floorRepository.findByFloorID(floorId).orElseThrow(() -> new ApiException("Floor id not found while get a Floor!"));
        return fromFloorEntity(floorEntity);
    }

    @Override
    public void updateAFloor(String floorId, Double foamThickness, String accessoryType, String packagingMaterial, Integer numberOfPiecesPerBox, Set<VariantRequest> variantRequestSet) {
        var floor = floorRepository.findByFloorID(floorId).orElseThrow(() -> new ApiException("Floor not found while updating!"));
        Set<String> variantIds = extractVariantIds(variantRequestSet);
        Floor floorUpdate = fromFloorEntity(foamThickness, accessoryType, packagingMaterial, numberOfPiecesPerBox, checkVariantRequestSet(variantRequestSet, variantRepository.findAllByVariantIdIn(variantIds)), floor);
        floorRepository.save(floorUpdate);
    }

    @Override
    public void deleteAFloor(String floorId) {
        var floor = floorRepository.findByFloorID(floorId).orElseThrow(()-> new ApiException("Floor not found while deleting process"));
        floorVariantRepository.deleteByFloor(floor);
        floorRepository.delete(floor);
    }

    @Override
    public PageResponse<FloorResponse> getAllFloorPageable(Pageable pageable) {
        var floors = floorRepository.findAll(pageable);
        if (floors.isEmpty()) throw new ApiException("Floor empty!");
        Page<FloorResponse> floorResponsePage = floors.map(FloorUtils::fromFloorEntity);
        return PageResponseBuilder.buildPageResponse(floorResponsePage);
    }

    private Floor createAFloorEntity(String productId, FloorRequest floorRequest) {
        var product = productRepository.findByProductId(productId).orElseThrow(() -> new ApiException("Product not found while create a Floor!"));
        Set<String> variantIds = extractVariantIds(floorRequest.getVariants());
        return createNewFloorEntity(product, floorRequest, checkVariantRequestSet(floorRequest.getVariants(), variantRepository.findAllByVariantIdIn(variantIds)));
    }

}
