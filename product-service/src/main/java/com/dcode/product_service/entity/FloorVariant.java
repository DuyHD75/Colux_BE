package com.dcode.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "floor_variant")
public class FloorVariant extends Auditable implements IVariant{

    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    private Double quantity;

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
        return quantity;
    }
}
