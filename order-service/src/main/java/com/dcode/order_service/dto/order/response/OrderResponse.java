package com.dcode.order_service.dto.order.response;


import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.dto.dashboard.response.DashboardResponse;
import com.dcode.order_service.dto.user.UserResponse;
import com.dcode.order_service.enumuration.PaymentMethod;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String code;
    private Integer status;
    private String toName;
    private String toPhone;
    private String toAddress;
    private String toWardName;
    private String toDistrictName;
    private String toProvinceName;
    @Nullable
    private OrderCancellationReasonResponse orderCancellationReason;
    @Nullable
    private String note;
    private UserResponse customer;
    private BigDecimal totalAmount;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal totalPay;
    private BigDecimal advancePayment;
    private PaymentMethod paymentMethod;
    private Integer paymentStatus;

    private List<CartVariantResponse.ClientVariantResponse> products;

}
