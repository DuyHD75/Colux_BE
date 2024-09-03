package com.dcode.order_service.entity.cashbook;

import com.dcode.order_service.entity.Auditable;
import com.dcode.order_service.enumuration.PaymentMethodType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonInclude(NON_DEFAULT)
@Table(name = "payment_method")
public class PaymentMethod extends Auditable {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodType code;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status;
}
