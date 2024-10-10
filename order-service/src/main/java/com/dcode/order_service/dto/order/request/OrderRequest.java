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
    private Long orderCancellationReasonId;
    @Nullable
    private String note;

    @NotNull(message = "Customer should be present")
    @NotEmpty(message = "Customer should not be empty")

    @NotBlank(message = "Customer should not be blank")
    private String customerId;

    @NotNull(message = "You should at least purchase one product")
    private List<PurchaseRequest> purchaseProducts;

    @Positive(message = "Total amount must be positive number")
    private BigDecimal totalAmount;

    @Positive(message = "Tax must be positive number")
    private BigDecimal tax;

    @Positive(message = "Shipping cost must be positive number")
    private BigDecimal shippingCost;

    @Positive(message = "Total pay must be positive number")
    private BigDecimal totalPay;

    @NotNull(message = "Payment method type must not be precised")
    private PaymentMethod paymentMethod;

    private Integer paymentStatus;
};