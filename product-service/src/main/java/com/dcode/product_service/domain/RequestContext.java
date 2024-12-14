package com.dcode.product_service.domain;

public class RequestContext {
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    private RequestContext() {
    }

    public static void start() {
        USER_ID.remove();
    }

    public static String getUserId() {
        return USER_ID.get();
    }

    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }
}