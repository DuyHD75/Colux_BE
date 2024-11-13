package com.dcode.product_service.dto;

import lombok.*;

@Data
public class CartDtoBase {

    private String variantDescription; //variantName
    private String categoryName;
    private String packageType;
    private Integer variantInventory;
    private Double priceSell;
    private ClientProductResponse productDetails;

    @Data
    public static class ClientProductResponse {
        private String productId;
        private String productName;
        private String productDescription;
        private String productImage; // chỉ cần 1 image hiển thị
        private String code;
        private PaintDetailsDto paintDetails;
        private WallpaperDetailsDto wallpaperDetails;
        private FloorDetailsDto floorDetails;
    }

    @Data
    public static class PaintDetailsDto {
        private String paintId;
        private String colorId;
        private String hex;
    } @Data
    public static class WallpaperDetailsDto {
        private String wallpaperId;
    } @Data
    public static class FloorDetailsDto {
        private String floorId;

    }
}

