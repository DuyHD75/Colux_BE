package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

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
    @NaturalId
    private String colorId;
    private String name;
    private String image;
    private String code;
    private String hex;
    private String LRV;
    private boolean interior;
    private boolean exterior;
    @Column(columnDefinition = "TEXT")
    private String description;
    private long colorTypeId;

    @ManyToMany
    @JoinTable(
            name = "Color_Collection",
            joinColumns = @JoinColumn(name = "color_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "collection_id", referencedColumnName = "id")
    )
    private List<Collection> collections;

    @OneToMany(mappedBy = "color")
    private List<Paint> paints;
}

