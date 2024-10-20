package com.dcode.order_service.entity.order;

import com.dcode.order_service.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

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

    private String productId;

    private String paintId;

    private String wallpaperId;

    private String floorId;

    private String variantId;

    private double quantity;

    private double trackingPrice;
}
