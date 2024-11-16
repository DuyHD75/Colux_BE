package com.dcode.identity_service.constant;

public class Constants {


    public class AuthorityConstant {
        public static final String ROLE = "role";
        public static final String DUY_CODE_LLC = "DUY_CODE_LLC";
        public static final String LOGIN_PATH = "/api/v1/users/login";
        public static final int EXPIRATION_DAYS = 90;
        public static final int STRENGTH = 12;
        public static final String EMPTY_VALUE = "empty";
        public static final String ROLE_PREFIX = "ROLE_";
        public static final String AUTHORITIES = "authorities";
        public static final String AUTHORITY_DELIMITER = ",";
        public static final String USER_AUTHORITIES = "review:create,product:read,review:update,review:delete";
        public static final String ADMIN_AUTHORITIES = "user:create,user:read,user:update,product:create,product:read,product:update,product:delete";
        public static final String MANAGER_AUTHORITIES = "product:create,product:read,product:update,product:delete,order:read,order:update";
        public static final String[] ALLOWED_PATHS = {
                "/api/v1/users/login",
                "/api/v1/users/register",
                "/api/v1/users/verify/account",
                "/api/v1/users/introspect",
                "/api/v1/users/password/reset",
                "/api/v1/users/password/reset/verify",
                "/api/v1/users/refresh-token",
                "/api/v1/users/logout",
                "/api/v1/users/reviews/info"

        };
    }

    public class EmailSubjectConstant {
        public static final String NEW_USER_ACCOUNT_VERIFICATION = "[Colux Alpha] - NEW USER ACCOUNT VERIFICATION";
        public static final String PASSWORD_RESET_REQUEST = "[Colux Alpha] - PASSWORD RESET REQUEST";
    }

}
