package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "properties")
public class Property extends Auditable {
    @Column(nullable = false,updatable = false, unique = true)
    private String propertyId;
    private String name;
    private String description;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PropertyValue> propertyValues;
}
