package com.dcode.product_service.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "wallpaper_variant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WallpaperVariant extends Auditable {
    @ManyToOne
    @JoinColumn(name = "wallpaper_id", nullable = false)
    private Wallpaper wallpaper;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    private Double quantity;
    private Double price;

}
