package com.dcode.order_service.dto.cart.response;

import lombok.Data;

@Data
public class CartVariantResponse {
    private Integer cartItemQuantity;
    private ClientVariantResponse cartItemVariant;

    @Data
    public static class ClientVariantResponse {
        private String variantId;
        private String variantDescription;
        private String categoryName;
        private String packageType;
        private Integer variantInventory;
        private Double priceSell;
        private Integer itemQuantity;
        private ClientProductResponse productDetails;

        @Data
        public static class ClientProductResponse {
            private String productId;
            private String productName;
            private String productDescription;
            private String productImage;
            private String code;
            private PaintDetails paintDetails;
            private WallpaperDetails wallpaperDetails;
            private FloorDetails floorDetails;


            @Data
            public static class PaintDetails {
                private String paintId;
                private String colorId;
                private String hex;
            }
            @Data
            public static class WallpaperDetails {
                private String wallpaperId;
            }
            @Data
            public static class FloorDetails {
                private String floorId;
            }

        }
    }
}