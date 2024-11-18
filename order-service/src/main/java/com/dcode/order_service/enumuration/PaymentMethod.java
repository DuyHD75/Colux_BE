package com.dcode.order_service.enumuration;

import java.util.stream.Stream;

public enum PaymentMethod {
    CASH("cash"),
    COD("cod"),
    PAYPAL("paypal");

    private String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentMethod fromValue(String value) {
        return Stream.of(PaymentMethod.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
