package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "floors")
public class Floor extends Auditable{

    private Integer width;
    private Integer height;
    private Integer thickness;
    private String material;
    private String color;

    @ManyToOne
    private Product product;

    @ManyToMany(mappedBy = "floors", cascade = CascadeType.ALL)
    private List<Variant> variants;


}
