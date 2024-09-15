package com.dcode.order_service.enumuration;

public enum TransactionIntent {
    CAPTURE,//CAPTURE: Indicates that the payment will be captured immediately.
    AUTHORIZE // AUTHORIZE: Indicates that the payment will be authorized but not captured until a later time.
}
