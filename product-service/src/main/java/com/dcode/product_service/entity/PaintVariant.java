package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "paint_variant", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"paint_id", "variant_id"})
})
public class PaintVariant extends Auditable {


    @JoinColumn(nullable = false, updatable = false, unique = true)
    private String paintVariantId;
    @ManyToOne
    @JoinColumn(name = "paint_id", nullable = false)
    private Paint paint;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    private Integer quantity;

    private Double price;



}
