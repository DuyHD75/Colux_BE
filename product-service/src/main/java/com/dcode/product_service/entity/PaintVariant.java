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
public class PaintVariant extends Auditable implements IVariant{

    @ManyToOne
    @JoinColumn(name = "paint_id", nullable = false)
    private Paint paint;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    private Integer quantity;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Override
    public String getVariantId() {
        return variant.getVariantId();
    }

    @Override
    public String getSizeName() {
        return variant.getSizeName();
    }

    @Override
    public String getCategoryName() {
        return variant.getCategoryName();
    }
    @Override
    public String getPackageType() {
        return variant.getPackageType();
    }
    @Override
    public Double getQuantity() {
        return quantity.doubleValue();
    }

}
