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
        public static final String[] ALLOWED_PATHS = {
//                "/api/v1/brands/public/**",
//                "/api/v1/categories/public/**",
//                "/api/v1/collections/public/**",
//                "/api/v1/collectionsTypes/public/**",
//                "/api/v1/colors/public/**",
                "/api/v1/products/**",



        };
    }

    public static final String BUCKET_NAME = "colux-alpha-storage.appspot.com";
    public static final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/";
}