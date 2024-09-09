package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "colors")
public class Color extends Auditable{
    @Column(nullable = false, updatable = false, unique = true)
    private String colorId;
    private String name;
    private String code;
    private String description;
    private long colorTypeId;

    @ManyToMany
    @JoinTable(
            name = "Color_Collection",
            joinColumns = @JoinColumn(name = "color_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "collection_id", referencedColumnName = "id")
    )
    private List<Collection> collections;

    @OneToOne(mappedBy = "color")
    private Paint paint;
}

