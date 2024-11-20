package com.dcode.order_service.service;


import com.dcode.order_service.dto.dashboard.response.DashboardResponse;
import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.GhnCalculateFeeRequest;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.ConfirmedOrderResponse;
import com.dcode.order_service.dto.order.response.GhnCalculateFeeResponse;
import com.dcode.order_service.dto.order.response.OrderResponse;
import com.dcode.order_service.entity.PageResponse;
import com.dcode.order_service.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IOrderService {
    void cancelOrder(String code);

    ConfirmedOrderResponse createClientOrder(OrderRequest request);

    void captureTransactionPaypal(String paymentId, String payerId) throws ResourceNotFoundException;

    List<OrderResponse> getAllOrders();

    boolean hasCustomerPurchasedProduct(String customerId, String productId);

    GhnCalculateFeeResponse calculateFee(GhnCalculateFeeRequest ghnCalculateFeeRequestRequest);

    Map<String, Object> getProvinces();

    Map<String, Object> getDistrict(JsonNode districtId);

    Map<String, Object> getWard(JsonNode wardId);

    Map<String, Object> getServices(JsonNode serviceRequest);

    List<OrderResponse> getOrdersByCustomerId(String customerId, String orderId);

    PageResponse<DashboardResponse.ProductDto> getTopProducts(Pageable pageable);
}
