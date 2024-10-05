package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.FloorRequest;
import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.FloorResponse;
import com.dcode.product_service.entity.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.dcode.product_service.utils.PaintUtils.convertVariantToVResponse;

public class FloorUtils {
    public static Floor createNewFloorEntity(Product product, FloorRequest floorRequest, Map<Variant, Pair<Double, Double>> variantRequestSet){
        Set<FloorVariant> floorVariants = new HashSet<>();
        Floor floor = Floor.builder()
                .floorID(UUID.randomUUID().toString())
                .product(product)
                .foamThickness(floorRequest.getFoamThickness())
                .accessoryType(floorRequest.getAccessoryType())
                .packagingMaterial(floorRequest.getPackagingMaterial())
                .numberOfPiecesPerBox(floorRequest.getNumberOfPiecesPerBox())
                .floorVariants(floorVariants)
                .build();

        for (Map.Entry<Variant, Pair<Double, Double>> entry: variantRequestSet.entrySet()){
            Variant variant = entry.getKey();
            Double quantity = entry.getValue().getLeft();
            Double price = entry.getValue().getRight();

           FloorVariant temp = FloorVariant.builder()
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
                .accessoryType(floor.getAccessoryType())
                .foamThickness(floor.getFoamThickness().toString())
                .numberOfPiecesPerBox(floor.getNumberOfPiecesPerBox().toString())
                .packagingMaterial(floor.getPackagingMaterial())
                .variants(convertVariantToVResponse(floor.getFloorVariants()))

                .build();
    }
    public static Floor fromFloorEntity(Double foamThickness, String accessoryType, String packagingMaterial, Integer numberOfPiecesPerBox, Map<Variant, Pair<Double, Double>> variantRequestSet, Floor floor){
        floor.setFoamThickness(foamThickness);
        floor.setAccessoryType(accessoryType);
        floor.setPackagingMaterial(packagingMaterial);
        floor.setNumberOfPiecesPerBox(numberOfPiecesPerBox);

        Set<FloorVariant> existingFloorVariants = floor.getFloorVariants();

        Set<FloorVariant> updatedFloorVariants = new HashSet<>();
        for (Map.Entry<Variant, Pair<Double, Double>> entry: variantRequestSet.entrySet()){
            Variant variant = entry.getKey();
            Double quantity = entry.getValue().getLeft();
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
