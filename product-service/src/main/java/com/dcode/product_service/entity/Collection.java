package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "collections")
@Entity
public class Collection extends Auditable{
    private String name;

    @ManyToMany(mappedBy = "collections")
    private List<Color> colors;

    @ManyToOne
    @JoinColumn(name = "color_family", referencedColumnName = "id")
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
