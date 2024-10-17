package com.dcode.order_service.entity.cart;

import com.dcode.order_service.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cart_variant")
public class CartVariantEntity extends Auditable {


    @ManyToOne
//    @MapsId("cardId")
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

}
