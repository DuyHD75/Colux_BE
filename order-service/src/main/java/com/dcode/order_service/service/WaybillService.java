package com.dcode.order_service.service;


import com.dcode.order_service.dto.order.request.GhnCallbackOrderRequest;
import com.dcode.order_service.dto.order.request.WaybillRequest;
import com.dcode.order_service.dto.order.response.WaybillResponse;

public interface WaybillService {


    WaybillResponse createAWaybill(WaybillRequest waybillRequest);

    WaybillResponse getWaybill(String waybillId);
}
