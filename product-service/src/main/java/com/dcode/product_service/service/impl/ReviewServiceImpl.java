package com.dcode.product_service.service.impl;

import com.dcode.product_service.dto.user.UserResponse;
import com.dcode.product_service.dtoRequest.ReviewRequest;
import com.dcode.product_service.dtoRequest.UserRequest;
import com.dcode.product_service.dtoResponse.ReviewResponse;
import com.dcode.product_service.entity.PageResponse;
import com.dcode.product_service.entity.PageResponseBuilder;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.entity.Review;
import com.dcode.product_service.enumeration.ReviewStatus;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.exception.BusinessException;
import com.dcode.product_service.proxy.ICustomerClientProxy;
import com.dcode.product_service.proxy.IOrderClientProxy;
import com.dcode.product_service.proxy.UserClientProxy;
import com.dcode.product_service.repository.ProductRepository;
import com.dcode.product_service.repository.ReviewRepository;
import com.dcode.product_service.service.IReviewService;
import com.dcode.product_service.utils.ReviewUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.ReviewUtils.fromReviewEntity;

@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
@Slf4j
@Service
public class ReviewServiceImpl implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ICustomerClientProxy clientProxy;
    private final UserClientProxy userClientProxy;
    private final IOrderClientProxy orderClientProxy;
    private final EntityManager entityManager;



    public ReviewResponse createAReview(ReviewRequest reviewRequest) {

        this.clientProxy.findUserByUserId(reviewRequest.getCustomerId())
                .orElseThrow(() -> new BusinessException("Cannot create cart :: No customer found with ID: " + reviewRequest.getCustomerId()));

        Boolean hasPurchased = orderClientProxy.hasCustomerPurchasedProduct(reviewRequest.getCustomerId(), reviewRequest.getProductId());
        if (!hasPurchased) {
            throw new ApiException("Customer has not purchased this product");
        }

        Review review = new Review();
        review.setReviewId(UUID.randomUUID().toString());
        review.setCustomerId(reviewRequest.getCustomerId());
        review.setScore(reviewRequest.getScore());
        review.setContent(reviewRequest.getContent());
        review.setStatus(ReviewStatus.APPROVED);

        // Set product
        Optional<Product> product = findProductByProductId(reviewRequest.getProductId());
        if (product.isPresent()) {
            review.setProduct(product.get());
        } else {
            throw new ApiException("Product not found");
        }

        // Set parent review if it's a reply
        if (reviewRequest.getParentId() != null) {
            Optional<Review> parentReview = findByReviewId(reviewRequest.getParentId());
            if (parentReview.isPresent() && canReply(parentReview.get())) {
                review.setParent(parentReview.get());
            } else {
                throw new ApiException("Cannot reply to this review");
            }
        } else {
            // If parentId is null, it's a root review
            review.setParent(null);
        }

        var saveReview = saveReview(review);
        if (reviewRequest.getParentId() == null) {
            var productReviews = reviewRepository.findAllByProduct_ProductId(reviewRequest.getProductId());
            Double score = productReviews.stream().mapToDouble(Review::getScore).average().orElse(0);

            Product productTemp = Optional.ofNullable(reviewRequest.getProductId())
                    .map(id -> entityManager.unwrap(Session.class)
                            .byNaturalId(Product.class)
                            .using("productId", id)
                            .getReference())
                    .orElse(null);
            Objects.requireNonNull(productTemp).setRatingAverage(score);
            productRepository.save(productTemp);
        }
        return saveReview;
    }


    public Optional<Product> findProductByProductId(String productId) {
        return productRepository.findByProductId(productId);
    }

    public Optional<Review> findByReviewId(String reviewId) {

        return reviewRepository.findByReviewId(reviewId);
    }

    public ReviewResponse saveReview(Review review) {
        return fromReviewEntity(reviewRepository.save(review));
    }

    @Override
    public PageResponse<ReviewResponse> getReviewsByProductId(String productId, Pageable pageable) {
        Optional<Product> product = productRepository.findByProductId(productId);
        if (product.isEmpty()) {
            throw new BusinessException("Product not found");
        }
        Page<Review> reviewsPage = reviewRepository.findAllByProduct_ProductId(productId, pageable);
        return convertReviews(reviewsPage, pageable, reviewsPage.getTotalElements());
        }

    @Override
    public PageResponse<ReviewResponse> getReviewsByUserId(String userId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findAllByCustomerId(userId, pageable);
        return convertReviews(reviewPage, pageable, reviewPage.getTotalElements());
    }

    private PageResponse<ReviewResponse> convertReviews(Page<Review> reviews, Pageable pageable, long totalElements) {
        if (reviews.isEmpty()) {
            throw new BusinessException("No comments found for the product");
        }
        List<Review> reviewList = reviews.getContent();
        List<Review> rootReviews = reviewList.stream()
                .filter(review -> review.getParent() == null)
                .toList();
        List<ReviewResponse> reviewResponses = rootReviews.stream()
                .map(review -> convertToDto(review, reviewList))
                .toList();

        List<UserRequest> userRequestList = reviewResponses.stream().map(
                reviewResponse -> new UserRequest(reviewResponse.getCustomerId())
        ).distinct().toList();

        List<UserResponse> userResponseList = userClientProxy.findUserReviewInfos(userRequestList);

        Map<String, UserResponse> userResponseMap = userResponseList.stream()
                .collect(Collectors.toMap(UserResponse::getUserId, userResponse -> userResponse));

        reviewResponses.forEach(reviewResponse -> {
            UserResponse userResponse = userResponseMap.get(reviewResponse.getCustomerId());
            if (userResponse != null) {
                reviewResponse.setUserInfo(userResponse);
            } else {
                log.warn("No user information found for customerId: {}", reviewResponse.getCustomerId());
//                throw new BusinessException("No user information found for customerId: {}", reviewResponse.getCustomerId());
            }
        });

        return PageResponseBuilder.buildPageResponseFromList(reviewResponses, pageable, totalElements);

    }

    private ReviewResponse convertToDto(Review review, List<Review> allReviews) {
        List<ReviewResponse> replies = allReviews.stream()
                .filter(r -> r.getParent() != null && r.getParent().getReviewId().equals(review.getReviewId()))
                .map(r -> convertToDto(r, allReviews))
                .toList();

        ReviewResponse reviewResponse = ReviewUtils.fromReviewEntity(review);
        reviewResponse.setReplies(replies);

        UserResponse userResponse = userClientProxy.findUserReviewInfos(
                List.of(new UserRequest(review.getCustomerId()))
        ).stream().findFirst().orElse(null);

        if (userResponse != null) {
            reviewResponse.setUserInfo(userResponse);
        } else {
            log.warn("No user information found for customerId: {}", review.getCustomerId());
        }

        return reviewResponse;
    }

    @Override
    public boolean canReply(Review review) {
        int level = 0;
        Review parent = review.getParent();
        while (parent != null) {
            level++;
            if (level >= 3) {
                return false;
            }
            parent = parent.getParent();
        }
        return true;
    }


}

