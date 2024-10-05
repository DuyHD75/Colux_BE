package com.dcode.microservices.api_gateway.constant;

public class Constants {
    public class AuthorityConstant{
        public static final String[] ALLOWED_PATHS = {
                "/identity-service/api/v1/users/.*",
                "/product-service/api/v1/products/.*"
        };
    }
}
