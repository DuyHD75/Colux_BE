package com.dcode.product_service.dtoRequest;

import com.dcode.product_service.enumeration.ReviewStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewRequest {
    private String reviewId;
    @NotNull
    private String productId;
    @NotNull
    private String customerId;
    @NotNull
    private String content;
    @Min(0)
    @Max(5)
    private Integer score;
    private ReviewStatus status;
    private String parentId;
}
