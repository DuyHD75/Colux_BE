package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "collections")
@Entity
public class Collection extends Auditable{

    @Column(nullable = false, updatable = false, unique = true)
    private String collectionId;
    private String name;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String image;
    private String hex;


    @ManyToMany(mappedBy = "collections")
    private Set<Color> colors;

    @ManyToOne(optional = true)
    @JoinColumn(name = "color_family_id", referencedColumnName = "id")
    private ColorFamily colorFamily;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @OneToOne
    @JoinColumn(name = "collection_type_id", referencedColumnName = "id")
    private CollectionType collectionType;

    @OneToOne
    @JoinColumn(name = "relative_collection_id", referencedColumnName = "id")
    private RelativeCollection relativeCollection;

}
