package com.dcode.order_service.entity.order;


import com.dcode.order_service.entity.Auditable;
import com.dcode.order_service.enumuration.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.NaturalId;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;


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
    @NaturalId
    private String orderId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

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

    @OneToMany(mappedBy = "orderEntity")
    private List<OrderLineEntity> orderLines;

    @Column(name = "total_amount", nullable = false, columnDefinition = "DECIMAL(15,5)")
    private BigDecimal totalAmount;
    @Column(name = "tax", nullable = false, columnDefinition = "DECIMAL(15,5)")

    private BigDecimal tax;
    @Column(name = "shipping_cost", nullable = false, columnDefinition = "DECIMAL(15,5)")
    private BigDecimal shippingCost;

    @Column(name ="total_pay", nullable = false, columnDefinition = "DECIMAL(15,5)")
    private BigDecimal totalPay;

    @Column(name = "payment_method_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "paypal_order_id")
    private String paypalOrderId;

    @Column(name= "payment_status") /* (1) Chưa thanh toán, (2) Đã thanh toán */
    private Integer paymentStatus;
}
