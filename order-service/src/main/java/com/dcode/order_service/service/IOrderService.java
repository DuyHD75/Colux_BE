package com.dcode.order_service.service;


import com.dcode.order_service.dto.order.request.OrderRequest;

public interface IOrderService {
    void cancelOrder(String code);

    void createClientOrder(OrderRequest request);

    void captureTransactionPaypal(String paypalOrderId, String payerId);

    Integer createNewOrder(OrderRequest request);
}
