package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.PaintRequest;
import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.PaintResponse;
import com.dcode.product_service.dtoResponse.VariantResponse;
import com.dcode.product_service.entity.Paint;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.entity.Variant;
import com.dcode.product_service.repository.VariantRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class PaintUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //    public static Set<VariantRequest> convertToVariantRequests(Set<String> variantRequestJsonSet)  {
//        Set<VariantRequest> variantRequests = new HashSet<>();
//    try {
//        for (String json : variantRequestJsonSet) {
//            VariantRequest variantRequest = objectMapper.readValue(json, VariantRequest.class);
//            variantRequests.add(variantRequest);
//        }
//    }catch (Exception e){
//        log.error("Error while convert variant request!");
//        throw new RuntimeException(e);
//    }
//
//        return variantRequests;
//    }
    public static PaintResponse fromPaintEntity(Paint paint) {
        Set<VariantResponse> variantResponse = new HashSet<>();
        if (paint.getVariants() != null) {
            paint.getVariants().forEach(variant -> {
                VariantResponse response = new VariantResponse();
                BeanUtils.copyProperties(variant, response);
                variantResponse.add(response);
            });
        }
        return PaintResponse.builder()
                .quantity("20")
                .color("red")
                .variants(variantResponse)
                .build();
    }

    public static Paint createNewPaintEntity(Product product, String quantity, String color, Set<Variant> variantRequestSet) {

        Paint paint = Paint.builder()
                .paintId(UUID.randomUUID().toString())
                .product(product)
                .quantity(Integer.parseInt(quantity))
//                .color(color) // colorId
                .variants(variantRequestSet)
                .build();
        variantRequestSet.forEach(variant -> {
            variant.getPaints().add(paint);
        });
        return paint;
    }

    public static Paint fromPaintEntityAndIgnoreField(PaintRequest paintRequest, Paint paint) {
        String[] ignoreFields = Arrays.stream(BeanUtils.getPropertyDescriptors(paintRequest.getClass()))
                .map(PropertyDescriptor::getName)
                .filter(name -> {
                    try {
                        return BeanUtils.getPropertyDescriptor(paintRequest.getClass(), name).getReadMethod().invoke(paintRequest) == null;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toArray(String[]::new);
        BeanUtils.copyProperties(paintRequest, paint, ignoreFields);

        return paint;
    }

    public static Map<String, Set<Variant>> updateVariants(Paint paint, Set<VariantRequest> variantRequestSet) {
        Set<Variant> currentVariants = paint.getVariants();
        Set<Variant> variantsToAdd = new HashSet<>();

        for (VariantRequest variantRequest : variantRequestSet) {
            boolean exists = currentVariants.stream()
                    .anyMatch(v -> v.getSizeName().equals(variantRequest.getSizeName()) &&
                            v.getCategoryName().equals(variantRequest.getCategoryName()) &&
                            v.getPackageType().equals(variantRequest.getPackageType()));
            if (!exists) {
                Variant newVariant = CreateVariant(variantRequest, paint);
                variantsToAdd.add(newVariant);
            }
        }
        currentVariants.addAll(variantsToAdd);
        Set<Variant> needToDelete = new HashSet<>();
        //delete variants not exist in new Set of variants
        for (Variant currentVariant : new HashSet<>(currentVariants)) {
            boolean stillExist = variantRequestSet.stream()
                    .anyMatch(v -> v.getSizeName().equals(currentVariant.getSizeName()) &&
                            v.getCategoryName().equals(currentVariant.getCategoryName()) &&
                            v.getPackageType().equals(currentVariant.getPackageType()));
            if (!stillExist) {
//                currentVariant.setPaint(null);
                needToDelete.add(currentVariant);
                currentVariants.remove(currentVariant);
            }
        }
        Map<String, Set<Variant>> result = new HashMap<>();
        result.put("needToDelete", needToDelete);
        result.put("newVariants", currentVariants);
        return result;
    }

    private static Variant CreateVariant(VariantRequest variantRequest, Paint paint) {
        Variant variant = Variant.builder()
                .variantId(UUID.randomUUID().toString())
                .sizeName(variantRequest.getSizeName())
                .categoryName(variantRequest.getCategoryName())
                .packageType(variantRequest.getPackageType())
//                .paint(paint)
                .build();
        return variant;
    }
}
