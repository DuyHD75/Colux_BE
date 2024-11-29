package com.dcode.product_service.dtoResponse;


import com.dcode.product_service.dto.user.UserResponse;
import com.dcode.product_service.entity.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReviewResponse {
    @JsonProperty("id")
    private String reviewId;
    private String customerId;
    private String productId;
    private Integer score;
    private String content;
    private String parentId;
    private List<ReviewResponse> replies;
    private LocalDateTime updatedAt;
    private UserResponse userInfo;

}
