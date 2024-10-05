package com.dcode.product_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorFamily extends Auditable {

    @Column(nullable = false, updatable = false, unique = true)
    @NaturalId
    private String colorFamilyId;
    private String name;
    private String title;
    private String description;
    private String hex;
    private String image;

    @OneToMany(mappedBy = "colorFamily")
    private Set<Collection> collections;
}
