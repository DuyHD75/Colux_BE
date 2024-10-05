package com.dcode.product_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "feature_values")
public class FeatureValue extends Auditable{

    @Column(updatable = false, unique = true, nullable = false)
    private String featureValueId;

    private String value;

    @ManyToOne
    @JsonIgnore
    private Feature feature;

    @ManyToMany(mappedBy = "featureValues")
    private List<Product> products;

}
