package com.dcode.order_service.constant;

public class Constants {

    public class AppConstants {
        public static final String ROLE = "role";
        public static final String DUY_CODE_LLC = "DUY_CODE_LLC";
//        public static final String HOST_URL = "http://localhost:8765/";
        public static final String HOST_URL = "https://colux.site/";
        public static final int EXPIRATION_DAYS = 90;
        public static final int STRENGTH = 12;
        public static final String EMPTY_VALUE = "empty";
        public static final String ROLE_PREFIX = "ROLE_";
        public static final String AUTHORITIES = "authorities";
        public static final String AUTHORITY_DELIMITER = ",";
        public static final String[] ALLOWED_PATHS = {
                "/api/v1/orders/create",
        };
        public static final String PAYPAL_BASE_URL = "https://api-m.sandbox.paypal.com";
        public static final double DEFAULT_TAX = 0.1;
        public static final int VND_TO_USD = 23_000;
        public static final String BRAND_NAME = "COLUX ALPHA";
//        public static final String FRONTEND_HOST = "https://colux.vercel.app";
        public static final String SERVICE_NAME = "order-service";

    }

    public class EmailSubjectConstant {
        public static final String CONFIRM_PLACED_ORDER = "[Colux Alpha] - ORDER PLACED SUCCESSFULLY";
        public static final String CONFIRM_CANCELED_ORDER = "[Colux Alpha] - ORDER CANCELED SUCCESSFULLY";
        public static final String CONFIRM_COMPLETED_ORDER = "[Colux Alpha] - ORDER COMPLETED SUCCESSFULLY";
    }

}
