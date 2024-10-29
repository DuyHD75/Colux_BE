package com.dcode.order_service.service;


import com.dcode.order_service.dto.order.request.GhnCallbackOrderRequest;
import com.dcode.order_service.dto.order.request.WaybillRequest;
import com.dcode.order_service.dto.order.response.WaybillResponse;

public interface WaybillService {

    void callbackStatusWaybillFromGHN(GhnCallbackOrderRequest ghnCallbackOrderRequest);

    WaybillResponse createAWaybill(WaybillRequest waybillRequest);
}
