package com.dcode.order_service.entity.order;

import com.dcode.order_service.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "order_line")
public class OrderLineEntity extends Auditable {

    @Column(updatable = false, nullable = false, unique = true)
    private String orderLineId;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private OrderEntity orderEntity;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "paint_id")
    private String paintId;

    @Column(name = "wallpaper_id")
    private String wallpaperId;

    @Column(name = "floor_id")
    private String floorId;

    @Column(name = "variant_id")
    private String variantId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false, columnDefinition = "DECIMAL(15,5)")
    private BigDecimal trackingPrice;

    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(15,5)")
    private BigDecimal amount;
}
