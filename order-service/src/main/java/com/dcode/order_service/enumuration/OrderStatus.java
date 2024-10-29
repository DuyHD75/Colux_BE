package com.dcode.order_service.enumuration;

public enum OrderStatus {
    CREATED(1),
    PENDING(2),
    APPROVED(3),
    COMPLETED(4),
    CANCELLED(5);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
