package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.ReviewRequest;
import com.dcode.product_service.dtoResponse.ReviewResponse;
import com.dcode.product_service.entity.PageResponse;
import com.dcode.product_service.entity.Review;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface IReviewService {
    ReviewResponse createAReview(@Valid ReviewRequest reviewRequest);

    PageResponse<ReviewResponse> getReviewsByProductId(String productId, Pageable pageable);

    boolean canReply(Review review);

    PageResponse<ReviewResponse> getReviewsByUserId(String userId, Pageable pageable);

//    PageResponse<ReviewResponse> getReviewsByProductId(String productId, Pageable pageable);
}
