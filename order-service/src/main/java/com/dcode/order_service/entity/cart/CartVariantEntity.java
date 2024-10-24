package com.dcode.order_service.entity.cart;

import com.dcode.order_service.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "cart_variant")
public class CartVariantEntity extends Auditable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    @Column(name = "variant_id", nullable = false)
    private String variantId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "paint_id")
    private String paintId;

    @Column(name = "wallpaper_id")
    private String wallpaperId;

    @Column(name = "floor_id")
    private String floorId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}