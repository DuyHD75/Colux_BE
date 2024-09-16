package com.dcode.order_service.entity.cart;

import com.dcode.order_service.entity.Auditable;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;


@Entity
@Builder
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "`carts`")
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CartEntity extends Auditable {

    @Column(name = "card_id", nullable = false, unique = true)
    private String cartId;


    @Column(name = "user_id", nullable = false)
    private String userId;


    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private String status;
}
