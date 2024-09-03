package com.dcode.order_service.entity.order;


import jakarta.persistence.*;
import jakarta.ws.rs.core.Variant;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Entity
@Table(name = "order_variant")
public class OrderVariant {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;


    @ManyToOne
    @MapsId("variantId")
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;




}
