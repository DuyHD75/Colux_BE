package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.ReviewRequest;
import com.dcode.product_service.dtoResponse.ReviewResponse;
import com.dcode.product_service.entity.Review;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.ReviewServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewServiceImpl reviewService;

    @PostMapping("/reviews")
    private ResponseEntity<Response> createAReview(@RequestBody @Valid ReviewRequest reviewRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            var review = reviewService.createAReview(reviewRequest);
            return ResponseEntity.ok().body(
                    getResponse(request, Map.of("Review", review), "Review created successfully!", OK)
            );
        } catch (ApiException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, HttpStatus.BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, exception, HttpStatus.INTERNAL_SERVER_ERROR));
        }

    }

    @GetMapping("/reviews/{productId}")
    public ResponseEntity<Response> getReviewsByProductId(@PathVariable String productId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            var reviews = reviewService.getReviewsByProductId(productId, pageable);
            return ResponseEntity.ok().body(
                    getResponse(request, Map.of("Review", reviews), "Review created successfully!", OK)
            );
        } catch (ApiException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, HttpStatus.BAD_REQUEST));
        } catch (Exception exception) {
            log.error("here", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, exception, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
