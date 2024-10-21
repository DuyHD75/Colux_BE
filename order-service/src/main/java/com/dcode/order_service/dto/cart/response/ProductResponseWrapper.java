package com.dcode.order_service.dto.cart.response;

import lombok.Data;

import java.util.List;

@Data
public class ProductResponseWrapper {
    private String time;
    private int code;
    private String path;
    private String status;
    private String message;
    private ProductData data;

    public static class ProductData {
        private List<CartVariantResponse.ClientVariantResponse> products;
    }
}
