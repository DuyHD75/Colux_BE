package com.dcode.product_service.exception;

import com.dcode.product_service.dtoResponse.ProductOrderResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class ApiException extends RuntimeException {
    private List<ProductOrderResponse> orderResponses;
    public ApiException() {
        super("An error occurred while processing your request. Please try again.");
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(String message, List<ProductOrderResponse> orderResponses) {
        super(message);
        this.orderResponses = orderResponses;
    }

    public ApiException(String message, List<ProductOrderResponse> orderResponses, Throwable cause) {
        super(message, cause);
        this.orderResponses = orderResponses;
    }
}
