package com.dcode.identity_service.utils;


public class EmailUtils {


    public static String getNewAccountEmailMessage(String name, String host, String serviceName, String apiPrefix, String key) {
        return "Hello " + name + ",\n\n" +
                "Welcome to our platform. To activate your account, please click on the link below:\n" +
                getAccountVerificationUrl(host, serviceName, apiPrefix, key) + "\n\n" +
                "Thank you for choosing us.\n" +
                "Best regards,\n" +
                "Support Team - Colux-System";
    }

    public static String getResetPasswordEmailMessage(String name, String host, String serviceName, String apiPrefix, String key) {
        return "Hello " + name + ",\n\n" +
                "You have requested to reset your password. To reset your password, please click on the link below:\n" +
                getResetPasswordUrl(host, serviceName, apiPrefix, key) + "\n\n" +
                "Thank you for choosing us.\n" +
                "Best regards,\n" +
                "Support Team - Colux-System";
    }

    public static String getAccountVerificationUrl(String host, String serviceName, String apiPrefix, String key) {
        return  host + serviceName + apiPrefix + "/verify/account?key=" + key;
    }

    public static String getResetPasswordUrl(String host, String serviceName, String apiPrefix, String key) {
        return host + serviceName + apiPrefix + "/verify/reset-password?key=" + key;
    }
}
