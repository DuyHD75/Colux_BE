package com.dcode.product_service.constant;

public class Constants {


    public class AppConstants {
        public static final String ROLE = "role";
        public static final String DUY_CODE_LLC = "DUY_CODE_LLC";
        public static final String HOST_URL = "http://localhost:8765/";
        public static final int EXPIRATION_DAYS = 90;
        public static final int STRENGTH = 12;
        public static final String EMPTY_VALUE = "empty";
        public static final String ROLE_PREFIX = "ROLE_";
        public static final String AUTHORITIES = "authorities";
        public static final String AUTHORITY_DELIMITER = ",";
        public static final String USER_AUTHORITIES = "review:create,product:read,review:update,review:delete";
        public static final String ADMIN_AUTHORITIES = "user:create,user:read,user:update,product:create,product:read,product:update,product:delete";
        public static final String MANAGER_AUTHORITIES = "product:create,product:read,product:update,product:delete";
        public static final String[] ALLOWED_PATHS = {
                "/api/v1/products/**",
                "/api/v1/products/purchase-order",
                "/api/v1/products/getProductByVariant",
                "/api/v1/products/categories/test/123",
                "/api/v1/products/product/getAll",
                "/api/v1/products/reduceProduct"
        };
        //        public static final String FRONTEND_HOST = "https://colux.vercel.app";
        public static final String SERVICE_NAME = "product-service";

    }


    public static final String BUCKET_NAME = "colux-alpha-storage.appspot.com";
    public static final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/";
}
