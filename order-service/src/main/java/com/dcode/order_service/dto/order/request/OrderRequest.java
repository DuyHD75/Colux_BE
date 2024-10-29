package com.dcode.order_service.dto.order.request;

import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.enumuration.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest {
    private String code;
    private Integer status;
    private String toName;
    private String toPhone;
    private String toAddress;
    private String toWardName;
    private String toDistrictName;
    private String toProvinceName;
    private String reference;
    @Nullable
    private String orderCancellationReasonId;
    @Nullable
    private String note;

    private String customerId;

    @NotNull(message = "You should at least purchase one product")
    private List<PurchaseRequest> purchaseProducts;

    @Positive(message = "Shipping cost must be positive number")
    private BigDecimal shippingCost;

    @NotNull(message = "Payment method type must not be precised")
    private PaymentMethod paymentMethod;

    private Integer paymentStatus;
};