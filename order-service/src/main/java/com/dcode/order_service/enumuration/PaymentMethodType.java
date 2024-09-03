package com.dcode.order_service.enumuration;

import java.util.stream.Stream;

public enum PaymentMethodType {
    CREDIT_CARD("credit_card"),
    CASH("cash"),
    PAYPAL("paypal");

    private String value;

    PaymentMethodType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static PaymentMethodType fromValue(String value) {
        return Stream.of(PaymentMethodType.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
