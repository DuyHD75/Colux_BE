package com.dcode.product_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "paints")

public class Paint extends Auditable{

    @JoinColumn(nullable = false, updatable = false, unique = true)
    private String paintId;

    @ManyToOne
    @JsonIgnore 
    private Product product;

    @OneToOne
    @JoinColumn(name = "color_id", referencedColumnName = "id")
    private Color color;

    @OneToMany(cascade = {PERSIST, MERGE}, mappedBy = "paint")
    private Set<PaintVariant> paintVariants;

}
