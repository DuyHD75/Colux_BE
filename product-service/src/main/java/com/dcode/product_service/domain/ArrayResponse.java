package com.dcode.product_service.domain;

import com.dcode.product_service.dtoResponse.ProductOrderResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record ArrayResponse(
        String path,
        String message,
        int status,
        List<ProductOrderResponse> data
) {
    public ArrayResponse(String path, String message, HttpStatus status, List<ProductOrderResponse> data) {
        this(path, message, status.value(), data);
    }
}