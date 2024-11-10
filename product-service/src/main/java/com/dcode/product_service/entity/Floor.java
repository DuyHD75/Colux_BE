package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "floors")
public class Floor extends Auditable{

//    private Integer width;
//    private Integer height;
//    private Integer thickness;
//    private String material;
//    private String color;

    @Column(nullable = false, updatable = false, unique = true)
    private String floorId;
    private Double foamThickness; // Xốp: 2mm
//
    private Integer numberOfPiecesPerBox; // Số tấm trong 1 hộp: 12 Tấm


    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status;

    @ManyToOne
    private Product product;

//    @JsonManagedReference
    @OneToMany(cascade = {PERSIST, MERGE}, mappedBy = "floor")
    private Set<FloorVariant> floorVariants;



}
