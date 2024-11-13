package com.dcode.microservices.api_gateway.constant;

public class Constants {
    public class AuthorityConstant{
        public static final String[] ALLOWED_PATHS = {
                "/identity-service/api/v1/users/.*",
                "/product-service/api/v1/products/.*",
                "/product-service/api/v1/upload/.*",
                "/order-service/api/v1/orders/shipping/.*",
                "/order-service/api/v1/chats/.*",
                "/order-service/ws/.*",

        };
    }
}
