package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallpapers")

public class Wallpaper extends Product{

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private Product product;

    private String dimensions;
    private String material;
    private String fireResistant;

}
