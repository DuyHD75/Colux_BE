package com.dcode.order_service.dto.order.request;

import lombok.Data;

import java.util.List;

@Data
public class GhnCalculateFeeRequest {
    private int fromDistrictId;
    private String fromWardCode;
    private int serviceId;
    private Integer serviceTypeId;
    private int toDistrictId;
    private String toWardCode;
    private int height;
    private int length;
    private int weight;
    private int width;
    private int insuranceValue;
    private int codFailedAmount;
    private String coupon;
    private List<Item> items;

    @Data
    public static class Item {
        private String name;
        private int quantity;
        private int height;
        private int weight;
        private int length;
        private int width;
    }
}
