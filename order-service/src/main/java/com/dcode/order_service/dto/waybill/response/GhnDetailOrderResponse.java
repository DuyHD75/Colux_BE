package com.dcode.order_service.dto.waybill.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;


@Data
public class GhnDetailOrderResponse {
    private int code;
    private String message;
    private GhnDataDetailOrder data;

    @Data
    public static class GhnDataDetailOrder {

        @JsonProperty("shop_id")
        private Integer shopId;

        @JsonProperty("client_id")
        private Integer clientId;

        @JsonProperty("return_name")
        private String returnName;

        @JsonProperty("return_phone")
        private String returnPhone;

        @JsonProperty("return_address")
        private String returnAddress;

        @JsonProperty("return_ward_code")
        private String returnWardCode;

        @JsonProperty("return_district_id")
        private Integer returnDistrictId;

        @JsonProperty("return_location")
        private Location returnLocation;

        @JsonProperty("from_name")
        private String fromName;

        @JsonProperty("from_phone")
        private String fromPhone;

        @JsonProperty("from_address")
        private String fromAddress;

        @JsonProperty("from_ward_code")
        private String fromWardCode;

        @JsonProperty("from_district_id")
        private Integer fromDistrictId;

        @JsonProperty("from_location")
        private Location fromLocation;

        @JsonProperty("deliver_station_id")
        private Integer deliverStationId;

        @JsonProperty("to_name")
        private String toName;

        @JsonProperty("to_phone")
        private String toPhone;

        @JsonProperty("to_address")
        private String toAddress;

        @JsonProperty("to_ward_code")
        private String toWardCode;

        @JsonProperty("to_district_id")
        private Integer toDistrictId;

        @JsonProperty("to_location")
        private Location toLocation;

        private Integer weight;
        private Integer length;
        private Integer width;
        private Integer height;

        @JsonProperty("converted_weight")
        private Integer convertedWeight;

        @JsonProperty("calculate_weight")
        private Integer calculateWeight;

        @JsonProperty("service_type_id")
        private Integer serviceTypeId;

        @JsonProperty("service_id")
        private Integer serviceId;

        @JsonProperty("payment_type_id")
        private Integer paymentTypeId;

        @JsonProperty("payment_type_ids")
        private List<Integer> paymentTypeIds;

        @JsonProperty("cod_amount")
        private BigDecimal codAmount;

        @JsonProperty("required_note")
        private String requiredNote;

        private String content;
        private String note;

        @JsonProperty("pickup_time")
        private Instant pickupTime;

        private List<Item> items;

        @JsonProperty("order_code")
        private String orderCode;

        @JsonProperty("version_no")
        private String versionNo;

        private String status;

        private List<Log> log;

        @JsonProperty("order_date")
        private Instant orderDate;

        private String leadtime;

        @JsonProperty("soc_id")
        private String socId;

        private List<String> tag;

        @JsonProperty("transportation_status")
        private String transportationStatus;

        @JsonProperty("transportation_phase")
        private String transportationPhase;

        @Data
        public static class Location {
            private double lat;
            private double lon;

            @JsonProperty("cell_code")
            private String cellCode;

            @JsonProperty("trust_level")
            private int trustLevel;

            private String wardcode;

            @JsonProperty("map_source")
            private String mapSource;
        }

        @Data
        public static class Item {
            private String name;
            private int quantity;
            private int length;
            private int width;
            private int height;

            private Map<String, Object> category;
            private String status;

            @JsonProperty("item_order_code")
            private String itemOrderCode;
        }

        @Data
        public static class Log {
            private String status;

            @JsonProperty("payment_type_id")
            private int paymentTypeId;

            @JsonProperty("trip_code")
            private String tripCode;

            @JsonProperty("updated_date")
            private Instant updatedDate;
        }
    }
}