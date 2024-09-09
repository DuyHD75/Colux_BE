package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.FloorResponse;
import com.dcode.product_service.entity.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.dcode.product_service.utils.PaintUtils.convertVariantToVResponse;

public class FloorUtils {
    public static Floor createNewFloorEntity(Product product, Double foamThickness, String accessoryType, String packagingMaterial, Integer numberOfPiecesPerBox, Map<Variant, Double> variantRequestSet){
        Set<FloorVariant> floorVariants = new HashSet<>();
        Floor floor = Floor.builder()
                .floorID(UUID.randomUUID().toString())
                .product(product)
                .foamThickness(foamThickness)
                .accessoryType(accessoryType)
                .packagingMaterial(packagingMaterial)
                .numberOfPiecesPerBox(numberOfPiecesPerBox)
                .floorVariants(floorVariants)
                .build();

        for (Map.Entry<Variant, Double> entry: variantRequestSet.entrySet()){
            Variant variant = entry.getKey();
            Double quantity = entry.getValue();

           FloorVariant temp = FloorVariant.builder()
                   .floor(floor)
                   .variant(variant)
                   .quantity(quantity)
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
    public static Floor fromFloorEntity(Double foamThickness, String accessoryType, String packagingMaterial, Integer numberOfPiecesPerBox, Map<Variant, Double> variantRequestSet, Floor floor){
        floor.setFoamThickness(foamThickness);
        floor.setAccessoryType(accessoryType);
        floor.setPackagingMaterial(packagingMaterial);
        floor.setNumberOfPiecesPerBox(numberOfPiecesPerBox);

        Set<FloorVariant> existingFloorVariants = floor.getFloorVariants();

        Set<FloorVariant> updatedFloorVariants = new HashSet<>();
        for (Map.Entry<Variant, Double> entry: variantRequestSet.entrySet()){
            Variant variant = entry.getKey();
            Double quantity = entry.getValue();

            FloorVariant floorVariant = existingFloorVariants.stream()
                    .filter(fv -> fv.getVariant().equals(variant))
                    .findFirst()
                    .orElse(null);

            if (floorVariant == null){
                floorVariant = FloorVariant.builder()
                        .floor(floor)
                        .variant(variant)
                        .quantity(quantity)
                        .build();
                updatedFloorVariants.add(floorVariant);
            } else {
                floorVariant.setQuantity(quantity);
                updatedFloorVariants.add(floorVariant);
            }
        }
        existingFloorVariants.removeIf(fv -> !variantRequestSet.containsKey(fv.getVariant()));
        floor.setFloorVariants(updatedFloorVariants);
        return floor;
    }
}
