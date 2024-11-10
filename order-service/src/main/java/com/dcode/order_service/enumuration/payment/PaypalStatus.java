package com.dcode.order_service.enumuration.payment;

public enum PaypalStatus {
    CREATED("created"),
    PENDING("pending"),
    SUCCESS("success"),
    FAILED("failed"),
    CANCELLED("cancelled");

    private final String status;
    PaypalStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
