package com.dcode.order_service.dto.cart.response;

import lombok.Data;

@Data
public class CartVariantResponse {
    private Integer cartItemQuantity;
    private ClientVariantResponse cartItemVariant;

    @Data
    public static class ClientVariantResponse {
        private String variantId;
        private String variantName;
        private String variantDescription;
        private Integer variantInventory;

        private ClientProductResponse variantProduct;

        @Data
        public static class ClientProductResponse {
            private String productId;
            private String productName;
            private String productImage;

            private PaintDetails paintDetails;
            private WallpaperDetails wallpaperDetails;
            private FloorDetails floorDetails;

            @Data
            public static class PaintDetails {
                private String paintId;
                private String paintName;
            }

            @Data
            public static class WallpaperDetails {
                private String wallpaperId;
                private String wallpaperName;
            }

            @Data
            public static class FloorDetails {
                private String floorId;
                private String floorName;
            }
        }
    }
}