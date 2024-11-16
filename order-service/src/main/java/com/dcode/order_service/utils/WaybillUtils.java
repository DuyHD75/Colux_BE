package com.dcode.order_service.utils;

import com.dcode.order_service.dto.order.request.WaybillRequest;
import com.dcode.order_service.dto.order.response.WaybillResponse;
import com.dcode.order_service.dto.waybill.response.WaybillLogResponse;
import com.dcode.order_service.entity.waybill.Waybill;
import com.dcode.order_service.entity.waybill.WaybillLog;
import com.dcode.order_service.enumuration.WaybillCallbackConstants;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public static WaybillResponse entityToResponse(Waybill waybill){
        return WaybillResponse.builder()
                .waybillId(waybill.getWaybillId())
                .shippingDate(waybill.getShippingDate())
                .weight(waybill.getWeight())
                .length(waybill.getLength())
                .width(waybill.getWidth())
                .height(waybill.getHeight())
                .note(waybill.getNote())
                .ghnRequiredNote(waybill.getGhnRequiredNote())
                .waybillLogs(waybill.getWaybillLogs().stream()
                        .map(WaybillUtils::getWaybillLogs)
                        .toList())
                .build();
    }



    private static String getStatusString(int status) {
        // Tạo một map đảo ngược từ mã trạng thái (number) thành tên trạng thái (String)
        for (Map.Entry<String, Integer> entry : WaybillCallbackConstants.WAYBILL_STATUS_CODE.entrySet()) {
            if (entry.getValue().equals(status)) {
                return entry.getKey();  // Trả về tên trạng thái
            }
        }
        return "Unknown";  // Nếu không tìm thấy trạng thái
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
}
