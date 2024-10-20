package com.dcode.product_service.dto;

import lombok.*;

import java.util.List;

@Data
public class CartDto {

    private String variantId;
    private String variantDescription; //variantName
    private String categoryName;
    private String packageType;
    private Integer variantInventory;
    private Double priceSell;
    private ProductDetailsDto productDetails;

    @Data
    public static class ProductDetailsDto {
        private String productId;
        private String productName;
        private String productImage; // chỉ cần 1 image hiển thị
        private String code;
        private PaintDetailsDto paintDetails;
    }

    @Data
    public static class PaintDetailsDto {
        private String colorId;
        private String hex;
    }
}

