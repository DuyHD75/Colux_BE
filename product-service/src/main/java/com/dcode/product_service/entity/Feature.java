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
@Table(name = "features")
@Entity

public class Feature extends Auditable {
    @Column(updatable = false, nullable = false, unique = true)
    private String featureId;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String category;

    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FeatureValue> featureValues;
}
