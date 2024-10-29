package com.dcode.order_service.utils;

import com.dcode.order_service.dto.order.request.WaybillRequest;
import com.dcode.order_service.dto.order.response.WaybillResponse;
import com.dcode.order_service.entity.waybill.Waybill;

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
                .build();
    }

}
