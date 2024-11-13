package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suppliers")
public class ProductSupplier extends Auditable{
    @Column(unique = true, updatable = false, nullable = false)
    private String supplierId;
    private String name;
    private String code;
    private String phone;
    private String email;

    @OneToMany(mappedBy = "productSupplier", cascade = CascadeType.ALL)
    private Set<Product> product;
}
