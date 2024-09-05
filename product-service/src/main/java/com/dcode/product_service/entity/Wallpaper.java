package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallpapers")

public class Wallpaper extends Auditable{

    @Column(unique = true, updatable = false, nullable = false)
    private String wallpaperId;
    @ManyToOne
    private Product product;
//
//    private String dimensions;
//    private String material;
//    private String fireResistant;
    private double area;

    @ManyToMany(mappedBy = "wallpapers", cascade = CascadeType.ALL)
    private Set<Variant> variants;

}
