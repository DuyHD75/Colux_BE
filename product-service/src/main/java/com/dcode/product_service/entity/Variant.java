package com.dcode.product_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "variants")
public class Variant extends Auditable{
    @Column(nullable = false, updatable = false, unique = true)
    private String variantId;
    private String sizeName;
    private String categoryName;
    private String packageType;

    @ManyToOne
    @JsonIgnore
    private Category category;

    @ManyToMany
    @JsonBackReference
    @JoinTable(
            name = "paint_variant",
            joinColumns = @JoinColumn(name = "variant_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "paint_id", referencedColumnName = "id")
    )
    private List<Paint> paints;

    @ManyToMany
    @JsonBackReference
    @JoinTable(
            name = "wallpaper_variant",
            joinColumns = @JoinColumn(name = "variant_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "wallpaper_id", referencedColumnName = "id")
    )
    private List<Wallpaper> wallpapers;

    @ManyToMany
    @JsonBackReference
    @JoinTable(
            name = "floor_variant",
            joinColumns = @JoinColumn(name = "variant_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "floor_id", referencedColumnName = "id")
    )
    private List<Floor> floors;
}
