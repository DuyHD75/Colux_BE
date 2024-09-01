package com.dcode.product_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

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

    private Integer quantity;

    @OneToOne
    @JoinColumn(name = "color_id", referencedColumnName = "id")
    private Color color;

    @ManyToMany(mappedBy = "paints", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Variant> variants;

}
