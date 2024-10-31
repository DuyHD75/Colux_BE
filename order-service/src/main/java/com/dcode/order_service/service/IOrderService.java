package com.dcode.order_service.service;


import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.GhnCalculateFeeRequest;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.ConfirmedOrderResponse;
import com.dcode.order_service.dto.order.response.GhnCalculateFeeResponse;
import com.dcode.order_service.exception.ResourceNotFoundException;

import java.util.List;

public interface IOrderService {
    void cancelOrder(String code);

    ConfirmedOrderResponse createClientOrder(OrderRequest request);

    void captureTransactionPaypal(String paymentId, String payerId) throws ResourceNotFoundException;

    List<Order> getAllOrders();

    boolean hasCustomerPurchasedProduct(String customerId, String productId);

    GhnCalculateFeeResponse calculateFee(GhnCalculateFeeRequest ghnCalculateFeeRequestRequest);

}
