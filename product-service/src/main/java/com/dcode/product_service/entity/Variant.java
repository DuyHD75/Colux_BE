package com.dcode.product_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "variants")
public class Variant extends Auditable {
    @Column(nullable = false, updatable = false, unique = true)
    @NaturalId
    private String variantId;
    private String sizeName;
    private String categoryName;
    private String packageType;

    @ManyToOne
    @JsonIgnore
    private Category category;

    //    @JsonBackReference
    @OneToMany(cascade = {PERSIST, MERGE}, mappedBy = "variant")
    private Set<PaintVariant> paintVariants;

    //    @JsonBackReference
    @OneToMany(cascade = {PERSIST, MERGE}, mappedBy = "variant")
    private Set<WallpaperVariant> wallpaperVariants;

//    @JsonBackReference
    @OneToMany(cascade = {PERSIST, MERGE}, mappedBy = "variant")
    private Set<FloorVariant> floorVariants;
}
