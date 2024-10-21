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
        private ClientProductResponse productDetails;

        @Data
        public static class ClientProductResponse {
            private String productId;
            private String productName;
            private String productImage;

            private PaintDetails paintDetails;


            @Data
            public static class PaintDetails {
                private String colorId;
                private String hex;
            }
        }
    }
}