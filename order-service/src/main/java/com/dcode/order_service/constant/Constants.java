package com.dcode.order_service.constant;

public class Constants {

    public class AppConstants {
        public static final String ROLE = "role";
        public static final String DUY_CODE_LLC = "DUY_CODE_LLC";
        public static final String HOST_URL = "http://localhost:8765";
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
                "/api/v1/users/login",
                "/api/v1/users/register",
                "/api/v1/users/verify/account",
                "/api/v1/users/introspect",
                "/api/v1/users/password/reset",
                "/api/v1/users/password/reset/verify",
                "/api/v1/users/refresh-token",
                "/api/v1/users/logout"
        };
        public static final String PAYPAL_BASE_URL = "https://api.sandbox.paypal.com";

    }

    public class EmailSubjectConstant {
        public static final String NEW_USER_ACCOUNT_VERIFICATION = "NEW USER ACCOUNT VERIFICATION";
        public static final String PASSWORD_RESET_REQUEST = "PASSWORD RESET REQUEST";
    }

}
