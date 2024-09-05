package com.dcode.product_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "brands")
public class Brand extends Auditable{
    @Column(updatable = false, nullable = false, unique = true)
    private String brandId;
    private String name;
    private String code;
    private String status;


    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Product> products;
}
