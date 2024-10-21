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
    private ProductData data;// object nì

    @Data
    public static class ProductData {
        private List<CartVariantResponse.ClientVariantResponse> products; // chỗ ni cậu lưu là gì array product á show lại cái json vs

    }
}