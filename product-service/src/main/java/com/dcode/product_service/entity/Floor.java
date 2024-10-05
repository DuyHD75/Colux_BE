package com.dcode.product_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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
    private String floorID;
    private Double foamThickness; // Xốp: 2mm
    private String accessoryType; // Len tường hoặc chỉ nẹp

    private String packagingMaterial; // Giấy cứng
    private Integer numberOfPiecesPerBox; // Số tấm trong 1 hộp: 12 Tấm
//    private Double areaPerBox; // Diện tích 1 hộp: 2.888 m² => should be in variant

    @ManyToOne
    private Product product;

//    @JsonManagedReference
    @OneToMany(cascade = {PERSIST, MERGE}, mappedBy = "floor")
    private Set<FloorVariant> floorVariants;



}
