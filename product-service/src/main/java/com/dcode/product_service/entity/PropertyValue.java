package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "property_values")
public class PropertyValue extends Auditable{

    @Column(nullable = false, unique = true, updatable = false)
    private String propertyValueId;

    private String value;

    @ManyToMany(mappedBy = "propertyValues")
    private List<Product> products;

    @ManyToOne
    private Property property;
}
