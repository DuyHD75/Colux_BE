package com.dcode.order_service.entity.order;


import com.dcode.order_service.entity.Auditable;
import com.dcode.order_service.enumuration.PaymentMethodType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "`orders`")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class OrderEntity extends Auditable {

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    private Long createdBy;
    private Long updatedBy;
    private String createdAt;
    private String updatedAt;

    private String userId;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status;

    @Column(name = "to_name", nullable = false)
    private String toName;

    @Column(name = "to_phone", nullable = false)
    private String toPhone;

    @Column(name = "to_address", nullable = false)
    private String toAddress;

    @Column(name = "to_ward_name", nullable = false)
    private String toWardName;

    @Column(name = "to_district_name", nullable = false)
    private String toDistrictName;

    @Column(name = "to_province_name", nullable = false)
    private String toProvinceName;

    @Nullable
    private String note;

    private BigDecimal totalAmount;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal totalPay;
    private PaymentMethodType paymentMethodType;
    private Integer paymentStatus;
}
