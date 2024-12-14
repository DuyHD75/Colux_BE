package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.ReviewRequest;
import com.dcode.product_service.dtoResponse.ReviewResponse;
import com.dcode.product_service.entity.Image;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.entity.Review;

import java.util.Set;
import java.util.UUID;

public class ReviewUtils {
    public static Review createReviewEntity(ReviewRequest reviewRequest, Product product) {
        return Review.builder()
                .reviewId(UUID.randomUUID().toString())
                .customerId(reviewRequest.getCustomerId())
                .content(reviewRequest.getContent())
                .score(reviewRequest.getScore())
                .product(product)
                .build();
    }
    public static ReviewResponse fromReviewEntity(Review review) {
        Set<Image> images = review.getProduct().getImages();
        String productImage = null;

        if (images != null && !images.isEmpty()) {
            Image firstImage = images.iterator().next();
            productImage = firstImage.getUrl();
        }
        ReviewResponse response = ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .customerId(review.getCustomerId())
                .productName(review.getProduct().getProductName())
                .productImage(productImage)
                .productId(review.getProduct().getProductId())
                .score(review.getScore())
                .content(review.getContent())
                .updatedAt(review.getUpdatedAt())
                .build();
        if (review.getParent() == null){
            response.setParentId(null);
        }else response.setParentId(review.getParent().getReviewId());
        return response;
    }
}

