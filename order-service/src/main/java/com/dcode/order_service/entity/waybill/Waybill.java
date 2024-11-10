package com.dcode.order_service.entity.waybill;


import com.dcode.order_service.entity.Auditable;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.enumuration.RequiredNote;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "waybill")
public class Waybill extends Auditable {

    @Column(name = "waybill_id)", nullable = false, updatable = false)
    private String waybillId;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    // Hiện tại, không thể xóa Waybill được
    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false, unique = true)
    private OrderEntity order;

    @Column(name = "shipping_date", nullable = false, updatable = false)
    private Instant shippingDate;

    @Column(name = "expected_delivery_time", nullable = false)
    private Instant expectedDeliveryTime;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status;

    @Column(name = "cod_amount", nullable = false)
    private Integer codAmount;

    @Column(name = "shipping_fee", nullable = false)
    private Integer shippingFee;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "length", nullable = false)
    private Integer length;

    @Column(name = "width", nullable = false)
    private Integer width;

    @Column(name = "height", nullable = false)
    private Integer height;

    @Column(name = "note")
    private String note;

    @Column(name = "ghn_payment_type_id", nullable = false)
    private Integer ghnPaymentTypeId;

    @Column(name = "ghn_required_note", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequiredNote ghnRequiredNote;

    @OneToMany(mappedBy = "waybill", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WaybillLog> waybillLogs = new ArrayList<>();
}
