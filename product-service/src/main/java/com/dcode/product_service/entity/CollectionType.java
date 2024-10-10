package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "collection_types")
@Entity
public class CollectionType extends Auditable{

    @Column(nullable = false, updatable = false, unique = true)
    @NaturalId
    private String collectionTypeId;

    private String name;

    @OneToOne(mappedBy = "collectionType")
    private Collection collection;
}
