package com.dcode.product_service.enumeration;

public enum PreorderStatus {
    WAITING_NOTIFICATION(0),
    NOTIFIED_AVAILABLE(1),
    CANCELLED_NOTIFICATION(2);

    private final int value;

    PreorderStatus(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static PreorderStatus fromValue(int value){
        switch (value){
            case 0: return WAITING_NOTIFICATION;
            case 1: return NOTIFIED_AVAILABLE;
            case 2: return CANCELLED_NOTIFICATION;
            default: throw new IllegalArgumentException("Unknown status: " + value);
        }
    }

}
