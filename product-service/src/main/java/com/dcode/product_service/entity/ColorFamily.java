package com.dcode.product_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorFamily extends Auditable {

    @Column(nullable = false, updatable = false, unique = true)
    @NaturalId
    private String ColorFamilyId;
    private String name;
    private String description;

    @OneToMany(mappedBy = "colorFamily")
    private List<Collection> collections;
}
