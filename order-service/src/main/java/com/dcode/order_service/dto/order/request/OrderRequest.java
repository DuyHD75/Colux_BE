package com.dcode.order_service.dto.order.request;

import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.entity.order.ShipmentEntity;
import com.dcode.order_service.enumuration.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "To phone number must not be blank")
    private String toPhone;
    @NotBlank(message = "To email must not be blank")
    @Email(message = "To email must be a valid email address")
    private String toEmail;

    @NotBlank(message = "To address must not be blank")
    private String toAddress;
    @NotBlank(message = "To ward name must not be blank")
    private String toWardName;
    @NotBlank(message = "To district name must not be blank")
    private String toDistrictName;
    @NotBlank(message = "To province name must not be blank")
    private String toProvinceName;
    @Nullable
    private String reference;
    @Nullable
    private String cancelReason;
    @Nullable
    private String note;

    private String employeeName;
    private String shippingImageURL;

    private String shipperName;

    private String customerId;

    @OneToOne(targetEntity = ShipmentEntity.class)
    private ShipmentEntity shipment;

    private BigDecimal advancePayment;

    @NotNull(message = "You should at least purchase one product")
    private List<PurchaseRequest> purchaseProducts;

    @Positive(message = "Shipping cost must be positive number")
    private BigDecimal shippingCost;

    @NotNull(message = "Payment method type must not be precised")
    private PaymentMethod paymentMethod;

    private Integer paymentStatus;
};