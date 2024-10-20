package com.dcode.order_service.entity.cart;

import com.dcode.order_service.entity.Auditable;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;


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

    @Column(name = "cart_id", updatable = false, nullable = false, unique = true)
    private String cartId;

    @Column(name = "customer_id", nullable = false, unique = true)
    private String customerId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private Set<CartVariantEntity> cartVariants = new HashSet<>();

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status;
}
