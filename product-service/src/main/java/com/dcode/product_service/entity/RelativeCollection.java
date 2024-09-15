package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "relative_collections")
@Entity
public class RelativeCollection extends Auditable {
    @Column(unique = true, updatable = false, nullable = false)
    @NaturalId
    private String relativeCollectionId;
    private String name;

    @OneToOne(mappedBy = "relativeCollection")
    private Collection collection;
}
