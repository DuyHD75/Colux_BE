package com.dcode.product_service.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wallpaper_variant", uniqueConstraints =
        {@UniqueConstraint(columnNames = {"wallpaper_id", "variant_id"})})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WallpaperVariant extends Auditable {

    @JoinColumn(nullable = false, updatable = false, unique = true)
    private String wallpaperVariantId;
    @ManyToOne
    @JoinColumn(name = "wallpaper_id", nullable = false)
    private Wallpaper wallpaper;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    private Integer quantity;
    private Double price;

}
