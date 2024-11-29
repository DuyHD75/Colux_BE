package com.dcode.order_service.utils;

import com.dcode.order_service.dto.order.request.WaybillRequest;
import com.dcode.order_service.dto.order.response.WaybillResponse;
import com.dcode.order_service.dto.waybill.response.WaybillLogResponse;
import com.dcode.order_service.entity.waybill.Waybill;
import com.dcode.order_service.entity.waybill.WaybillLog;
import com.dcode.order_service.enumuration.WaybillCallbackConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class WaybillUtils {
    public static Waybill requestToEntity(WaybillRequest waybillRequest){
        return Waybill.builder()
                .waybillId(UUID.randomUUID().toString())
                .shippingDate(waybillRequest.getShippingDate())
                .weight(waybillRequest.getWeight())
                .length(waybillRequest.getLength())
                .width(waybillRequest.getWidth())
                .height(waybillRequest.getHeight())
                .note(waybillRequest.getNote())
                .ghnRequiredNote(waybillRequest.getGhnRequiredNote())
                .build();
    }

    public static WaybillResponse entityToResponse(Waybill waybill) {
    List<WaybillLogResponse> waybillLogs = waybill.getWaybillLogs().stream()
            .map(WaybillUtils::getWaybillLogs)
            .collect(Collectors.toList());

    return WaybillResponse.builder()
            .code(waybill.getCode())
            .waybillId(waybill.getWaybillId())
            .shippingDate(waybill.getShippingDate())
            .weight(waybill.getWeight())
            .length(waybill.getLength())
            .width(waybill.getWidth())
            .height(waybill.getHeight())
            .note(waybill.getNote())
            .ghnRequiredNote(waybill.getGhnRequiredNote())
            .waybillLogs(waybillLogs)
            .build();
}
    private static WaybillLogResponse getWaybillLogs(WaybillLog waybill) {
        String previousStatusString = getStatusString(waybill.getPreviousStatus());
        String currentStatusString = getStatusString(waybill.getCurrentStatus());

        return WaybillLogResponse.builder()
                .waybillLogId(waybill.getWaybillLogId())
                .previousStatus(previousStatusString) // Chuyển đổi trạng thái trước
                .currentStatus(currentStatusString)   // Chuyển đổi trạng thái hiện tại
                .createdAt(waybill.getCreatedAt())
                .build();
    }

    private static String getStatusString(int status) {
        if (status == 0) {
            return "Order has been created";  // Special case for status 0
        }

        for (Map.Entry<String, Integer> entry : WaybillCallbackConstants.WAYBILL_STATUS_CODE.entrySet()) {
            if (entry.getValue().equals(status)) {
                return getStatusDescription(entry.getKey()); // Return only the description
            }
        }
        return "Unknown - Status not found"; // Default for undefined status
    }

    private static String getStatusDescription(String statusKey) {
        // Return descriptions in English
        switch (statusKey) {
            case "ready_to_pick":
                return "Order has been created and is waiting for pickup";
            case "picking":
                return "Staff is picking up the order";
            case "cancel":
                return "Order has been canceled";
            case "money_collect_picking":
                return "Collecting money from the sender";
            case "picked":
                return "Staff has picked up the order successfully";
            case "transporting":
                return "Order is being transported";
            case "sorting":
                return "Order is being sorted";
            case "delivering":
                return "Staff is delivering the order";
            case "money_collect_delivering":
                return "Collecting money from the recipient";
            case "delivered":
                return "Order has been delivered successfully";
            case "delivery_fail":
                return "Delivery failed";
            case "waiting_to_return":
                return "Waiting to return the order to the sender";
            case "return":
                return "Returning the order to the sender";
            case "return_transporting":
                return "Order is being returned";
            case "return_sorting":
                return "Order is being sorted for return";
            case "returning":
                return "Staff is returning the order";
            case "return_fail":
                return "Return failed";
            case "returned":
                return "Order has been returned successfully";
            case "exception":
                return "Order encountered an exception";
            case "damage":
                return "Order is damaged";
            case "lost":
                return "Order is lost";
            default:
                return "Status not identified";
        }
    }

}
