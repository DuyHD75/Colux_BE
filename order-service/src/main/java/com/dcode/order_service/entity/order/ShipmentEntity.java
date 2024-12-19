package com.dcode.order_service.entity.order;

import com.dcode.order_service.entity.Auditable;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "`shipments`")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ShipmentEntity extends Auditable {

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "shipment_id", updatable = false, nullable = false, unique = true)
    private String shipmentId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "to_address", nullable = false)
    private String toAddress;

    @Column(name = "to_ward_name", nullable = false)
    private String toWardName;

    @Column(name = "to_district_name", nullable = false)
    private String toDistrictName;

    @Column(name = "to_province_name", nullable = false)
    private String toProvinceName;

    @Column(name = "to_ward_code", nullable = false)
    private String toWardCode;

    @Column(name = "to_district_id", nullable = false)
    private String toDistrictId;

    @Column(name = "to_province_id", nullable = false)
    private String toProvinceId;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status; // (1) - default
}
