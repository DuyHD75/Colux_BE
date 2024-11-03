package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.FloorRequest;
import com.dcode.product_service.dtoResponse.FloorResponse;
import com.dcode.product_service.entity.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.dcode.product_service.utils.PaintUtils.convertVariantToVResponse;

public class FloorUtils {
    public static Floor createNewFloorEntity(Product product, FloorRequest floorRequest, Map<Variant, Pair<Integer, Double>> variantRequestSet){
        Set<FloorVariant> floorVariants = new HashSet<>();
        Floor floor = Floor.builder()
                .floorId(UUID.randomUUID().toString())
                .product(product)
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
                .foamThickness(floor.getFoamThickness().toString())
                .numberOfPiecesPerBox(floor.getNumberOfPiecesPerBox().toString())
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
}
