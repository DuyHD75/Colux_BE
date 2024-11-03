package com.dcode.order_service.dto.order.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GhnCalculateFeeRequest {
    @JsonProperty("from_district_id")
    private int fromDistrictId;

    @JsonProperty("from_ward_code")
    private String fromWardCode;

    @JsonProperty("service_id")
    private int serviceId;

    @JsonProperty("service_type_id")
    private Integer serviceTypeId;

    @JsonProperty("to_district_id")
    private int toDistrictId;

    @JsonProperty("to_ward_code")
    private String toWardCode;

    private int height;
    private int length;
    private int weight;
    private int width;

    @JsonProperty("insurance_value")
    private int insuranceValue;

    @JsonProperty("cod_failed_amount")
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