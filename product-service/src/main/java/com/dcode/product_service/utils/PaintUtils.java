package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.PaintResponse;
import com.dcode.product_service.dtoResponse.VariantResponse;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.ProductUtils.fromProductEntity;
import static com.dcode.product_service.utils.ProductUtils.fromProductEntitySimple;

@Slf4j
@AllArgsConstructor
public class PaintUtils {

    public static PaintResponse fromPaintEntity(Paint paint) {
        return PaintResponse.builder()
                .color(paint.getColor().getName())
                .variants(convertVariantToVResponse(paint.getPaintVariants()))
                .product(fromProductEntitySimple(paint.getProduct()))
                .build();

    }

    public static List<VariantResponse> convertVariantToVResponse(Set<? extends IVariant> variants) {
        return variants.stream()
                .map(variant -> VariantResponse.builder()
                        .variantId(variant.getVariantId())
                        .sizeName(variant.getSizeName())
                        .categoryName(variant.getCategoryName())
                        .packageType(variant.getPackageType())
                        .build())
                .collect(Collectors.toList());
    }

    public static Map<Variant, Pair<Double, Double>> checkVariantRequestSet(Set<VariantRequest> variantRequestSet, Set<Variant> variantSetInDb) {
        Set<String> foundVariantIds = variantSetInDb.stream().map(Variant::getVariantId).collect(Collectors.toSet());

        Set<String> notFoundVariantIds = variantRequestSet.stream()
                .map(VariantRequest::getVariantId)
                .filter(variantId -> !foundVariantIds.contains(variantId))
                .collect(Collectors.toSet());

        if (!notFoundVariantIds.isEmpty()) {
            throw new ApiException("The following Variant IDs were not found: " + notFoundVariantIds);
        }
        return variantSetInDb.stream()
                .collect(Collectors.toMap(
                        variant -> variant, // Key là Variant
                        variant -> variantRequestSet.stream()
                                .filter(request -> request.getVariantId().equals(variant.getVariantId()))
                                .findFirst()
                                .map(request -> Pair.of(request.getQuantity(), request.getPrice()))
                                .orElseThrow(() -> new ApiException("Quantity not found for Variant ID: " + variant.getVariantId()))
                ));
    }

    public static Paint createNewPaintEntity(Product product, Color color, Map<Variant, Pair<Double, Double>> variantRequestSet) {
        Set<PaintVariant> paintVariant = new HashSet<>();
        Paint paint = Paint.builder()
                .paintId(UUID.randomUUID().toString())
                .product(product)
                .color(color) // colorId
                .paintVariants(paintVariant)
                .build();

        for (Map.Entry<Variant, Pair<Double, Double>> entry : variantRequestSet.entrySet()) {
            Variant variant = entry.getKey();
            Integer quantity = entry.getValue().getLeft().intValue();
            Double price = entry.getValue().getRight();

            PaintVariant temp = PaintVariant.builder()
                    .paint(paint)
                    .variant(variant)
                    .quantity(quantity)
                    .price(price)
                    .build();
            paint.getPaintVariants().add(temp);
        }
        return paint;
    }

    public static Paint fromPaintEntity(String color, Map<Variant, Pair<Double, Double>> variantRequestSet, Paint paint) {
//        paint.setColor(color);
        Set<PaintVariant> existingPaintVariants = paint.getPaintVariants();

        //convert Double value
        Map<Variant, Pair<Integer, Double>> variantQuantityPriceMap = variantRequestSet.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Pair.of(entry.getValue().getLeft().intValue(), entry.getValue().getRight())
                ));
        Set<PaintVariant> updatedPaintVariants = new HashSet<>();
        // check variant bw request and db, exist -> check quantity, db dont have -> add, request dont have -> remove
        for (Map.Entry<Variant, Pair<Integer, Double>> entry : variantQuantityPriceMap.entrySet()) {
            Variant variant = entry.getKey();
            Integer quantity = entry.getValue().getLeft();
            Double price = entry.getValue().getRight();

            PaintVariant paintVariant = existingPaintVariants.stream()
                    .filter(pv -> pv.getVariant().equals(variant))
                    .findFirst()
                    .orElse(null);

            if (paintVariant == null) {
                paintVariant = PaintVariant.builder()
                        .paint(paint)
                        .variant(variant)
                        .quantity(quantity)
                        .price(price)
                        .build();
                updatedPaintVariants.add(paintVariant);
            } else {
                paintVariant.setQuantity(quantity);
                paintVariant.setPrice(price);
                updatedPaintVariants.add(paintVariant);
            }
        }
        existingPaintVariants.removeIf(pv -> !variantQuantityPriceMap.containsKey(pv.getVariant()));
        paint.setPaintVariants(updatedPaintVariants);
        return paint;
    }

    public static Set<String> extractVariantIds(Set<VariantRequest> variantRequestSet) {
        return variantRequestSet.stream().map(
                        VariantRequest::getVariantId)
                .collect(Collectors.toSet());
    }
}
