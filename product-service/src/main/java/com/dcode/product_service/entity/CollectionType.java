package com.dcode.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "collection_types")
@Entity
public class CollectionType extends Auditable{
    private String name;

    @OneToOne
    @JoinColumn(name = "collection_id", referencedColumnName = "id")
    private Collection collection;
}
