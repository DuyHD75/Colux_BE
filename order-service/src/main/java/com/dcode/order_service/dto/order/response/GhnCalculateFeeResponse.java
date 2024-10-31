package com.dcode.order_service.dto.order.response;

import lombok.Data;

@Data
public class GhnCalculateFeeResponse {
    private int code;
    private String message;
    private Data data;

    @lombok.Data
    public static class Data {
        private int total;
        private int serviceFee;
        private int insuranceFee;
        private int pickStationFee;
        private int couponValue;
        private int r2sFee;
        private int returnAgain;
        private int documentReturn;
        private int doubleCheck;
        private int codFee;
        private int pickRemoteAreasFee;
        private int deliverRemoteAreasFee;
        private int codFailedFee;

    }
}