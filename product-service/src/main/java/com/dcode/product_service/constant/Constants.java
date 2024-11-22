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
                "/api/v1/brands/public/**",
                "/api/v1/categories/public/**",
                "/api/v1/collections/public/**",
                "/api/v1/collectionsTypes/public/**",
                "/api/v1/colors/public/**",
                "/api/v1/colorFamilies/public/**",
                "/api/v1/features/public/**",
                "/api/v1/floors/public/**",
                "/api/v1/images/public/**",
                "/api/v1/paints/public/**",
                "/api/v1/preorders/public/**",
                "/api/v1/products/public/**",
                "/api/v1/products/purchase-order",
                "/api/v1/products/getProductByVariant",
                "/api/v1/products/getInfo",
                "/api/v1/products/getProductDashboard",
                "/api/v1/products/reduceProduct",
                "/api/v1/products/getDashboardInfo",
                "/api/v1/properties/public/**",
                "/api/v1/relativeCollections/public/**",
                "/api/v1/reviews/public/**",
                "/api/v1/rooms/public/**",
                "/api/v1/searches/public/**",
                "/api/v1/suppliers/public/**",
                "/api/v1/variants/public/**",
                "/api/v1/wallpapers/public/**",
                "/api/v1/upload/**",
        };
        //        public static final String FRONTEND_HOST = "https://colux.vercel.app";
        public static final String SERVICE_NAME = "product-service";
    }

    public static final String BUCKET_NAME = "colux-alpha-storage.appspot.com";
    public static final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/";
}
