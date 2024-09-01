package com.dcode.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private String value;

    @ManyToMany(mappedBy = "propertyValues")
    private List<Product> products;

    @ManyToOne
    private Property property;
}
