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
        public static final String[] ALLOWED_PATHS = {};
        public static final String PAYPAL_BASE_URL = "https://api-m.sandbox.paypal.com";
        public static final double DEFAULT_TAX = 0.1;
        public static final int VND_TO_USD = 23_000;
        public static final String BRAND_NAME = "COLUX ALPHA";
        //        public static final String FRONTEND_HOST = "https://colux.vercel.app";
        public static final String SERVICE_NAME = "order-service";

    }


    public static final String BUCKET_NAME = "colux-alpha-storage.appspot.com";
    public static final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/";
}
