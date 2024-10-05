package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.PreorderRequest;
import com.dcode.product_service.dtoResponse.PreorderResponse;
import com.dcode.product_service.entity.Preorder;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.entity.Variant;
import com.dcode.product_service.enumeration.PreorderStatus;

import java.time.Instant;
import java.util.UUID;

public class PreorderUtils {
    public static Preorder createNewPreorderEntity(PreorderRequest preorderRequest, Product product, Variant variant){
        return Preorder.builder()
                .preorderId(UUID.randomUUID().toString())
                .quantity(preorderRequest.getQuantity())
                .preorderDate(Instant.now())
                .identity(preorderRequest.getIdentity())
                .status(PreorderStatus.WAITING_NOTIFICATION.getValue())
                .product(product)
                .variant(variant)
                .build();
    }
    public static PreorderResponse fromPreorderEntity(Preorder preorder){
        return PreorderResponse.builder()
                .preorderId(preorder.getPreorderId())
                .quantity(preorder.getQuantity())
                .identity(preorder.getIdentity())
                .build();
    }

}
