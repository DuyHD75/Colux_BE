package com.dcode.order_service.dto.order;

import com.dcode.order_service.dto.order.response.OrderResourceResponse;
import com.dcode.order_service.dto.order.response.OrderVariantResponse;
import com.dcode.order_service.enumuration.PaymentMethodType;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Set;


@Data
public class Order {
    private Long id;
    private String orderId;
    private Long createdBy;
    private Long updatedBy;
    private String createdAt;
    private String updatedAt;
    private String code;
    private Integer status;
    private String toName;
    private String toPhone;
    private String toAddress;
    private String toWardName;
    private String toDistrictName;
    private String toProvinceName;
    private OrderResourceResponse orderResource;
    @Nullable
    private String note;
    private Long userId;
    private Set<OrderVariantResponse> orderVariants;
    private BigDecimal totalAmount;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal totalPay;
    private PaymentMethodType paymentMethodType;
    private Integer paymentStatus;
}
