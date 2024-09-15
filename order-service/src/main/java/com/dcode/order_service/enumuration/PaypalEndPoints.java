package com.dcode.order_service.enumuration;

public enum PaypalEndPoints {

    GET_ACCESS_TOKEN("/v1/oauth2/token"),
    GET_CLIENT_TOKEN("/v1/identity/generate-token"),
    ORDER_CHECKOUT("/v2/checkout/orders");

    private final String path;

    PaypalEndPoints(String path) {
        this.path = path;
    }
    public static String createURL(String baseURL, PaypalEndPoints endPoint) {
        return baseURL + endPoint.path;
    }

    public static String createCaptureURL(String baseURL, PaypalEndPoints endPoint, String token) {
        return baseURL + endPoint.path + "/" + token + "/capture";
    }
}
