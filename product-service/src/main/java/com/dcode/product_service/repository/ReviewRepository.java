package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByReviewId(String reviewId);
    Page<Review> findAllByProduct_ProductId(String productId, Pageable pageable);
    List<Review> findAllByProduct_ProductId(String productId);

    Page<Review> findAllByCustomerId(String userId, Pageable pageable);
}
