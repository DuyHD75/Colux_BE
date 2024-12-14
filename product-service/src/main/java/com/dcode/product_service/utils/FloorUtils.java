package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.FloorRequest;
import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.FloorResponse;
import com.dcode.product_service.entity.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.dcode.product_service.utils.PaintUtils.convertVariantToVResponse;

public class FloorUtils {

    public static FloorVariant createNewFloorVariant (Floor floor, Variant variant, Integer quantity, Double price){
        return FloorVariant.builder()
                .floorVariantId(UUID.randomUUID().toString())
                .floor(floor)
                .variant(variant)
                .quantity(quantity)
                .price(price)
                .build();
    }

    public static Floor createNewFloorEntity(Product product, FloorRequest floorRequest, Map<Variant, Pair<Integer, Double>> variantRequestSet){
        Set<FloorVariant> floorVariants = new HashSet<>();
        Floor floor = Floor.builder()
                .floorId(UUID.randomUUID().toString())
                .product(product)
                .status(1)
                .foamThickness(floorRequest.getFoamThickness())
                .numberOfPiecesPerBox(floorRequest.getNumberOfPiecesPerBox())
                .floorVariants(floorVariants)
                .build();

        for (Map.Entry<Variant, Pair<Integer, Double>> entry: variantRequestSet.entrySet()){
            Variant variant = entry.getKey();
            Integer quantity = entry.getValue().getLeft();
            Double price = entry.getValue().getRight();

           FloorVariant temp = FloorVariant.builder()
                   .floorVariantId(UUID.randomUUID().toString())
                   .floor(floor)
                   .variant(variant)
                   .quantity(quantity)
                   .price(price)
                   .build();
           floor.getFloorVariants().add(temp);
        }
        return floor;
    }

    public static FloorResponse fromFloorEntity(Floor floor){
        return FloorResponse.builder()
                .floorId(floor.getFloorId())
                .foamThickness(floor.getFoamThickness())
                .numberOfPiecesPerBox(floor.getNumberOfPiecesPerBox())
                .variants(convertVariantToVResponse(floor.getFloorVariants()))
                .status(floor.getStatus())
                .build();
    }
    public static Floor fromFloorEntity(FloorRequest floorRequest, Map<Variant, Pair<Integer, Double>> variantRequestSet, Floor floor){
        floor.setFoamThickness(floorRequest.getFoamThickness());
        floor.setNumberOfPiecesPerBox(floorRequest.getNumberOfPiecesPerBox());

        Set<FloorVariant> existingFloorVariants = floor.getFloorVariants();

        Set<FloorVariant> updatedFloorVariants = new HashSet<>();
        for (Map.Entry<Variant, Pair<Integer, Double>> entry: variantRequestSet.entrySet()){
            Variant variant = entry.getKey();
            Integer quantity = entry.getValue().getLeft();
            Double price = entry.getValue().getRight();

            FloorVariant floorVariant = existingFloorVariants.stream()
                    .filter(fv -> fv.getVariant().equals(variant))
                    .findFirst()
                    .orElse(null);

            if (floorVariant == null){
                floorVariant = FloorVariant.builder()
                        .floor(floor)
                        .variant(variant)
                        .quantity(quantity)
                        .price(price)
                        .build();
                updatedFloorVariants.add(floorVariant);
            } else {
                floorVariant.setQuantity(quantity);
                floorVariant.setPrice(price);
                updatedFloorVariants.add(floorVariant);
            }
        }
        existingFloorVariants.removeIf(fv -> !variantRequestSet.containsKey(fv.getVariant()));
        floor.setFloorVariants(updatedFloorVariants);
        return floor;
    }

    public static FloorRequest createFloorRequest(String productId, Double foamThickness, Integer numberPiecePerBox, String variantId, Integer quantity, Double price) {
        VariantRequest variantRequest = VariantRequest.builder()
                .variantId(variantId)
                .quantity(quantity)
                .price(price)
                .build();
        return FloorRequest.builder()
                .productId(productId)
                .foamThickness(foamThickness)
                .variants(Collections.singleton(variantRequest))
                .numberOfPiecesPerBox(numberPiecePerBox)
                .build();
    }
}
