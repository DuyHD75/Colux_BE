package com.dcode.product_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallpapers")

public class Wallpaper extends Auditable {

    @Column(unique = true, updatable = false, nullable = false)
    private String wallpaperId;
    @ManyToOne
    private Product product;
    //
//    private String dimensions;
//    private String material;
//    private String fireResistant;
//    private double area;

    //    @JsonManagedReference
    @OneToMany(cascade = {PERSIST, MERGE}, mappedBy = "wallpaper")
    private Set<WallpaperVariant> wallpaperVariants;


}
