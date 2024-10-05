package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotions")
public class Promotion extends Auditable {

    @JoinColumn(nullable = false, updatable = false, unique = true)
    private Long promotionId;

    private String description;
    private Instant startDate;
    private Instant endDate;
    private Integer discountPercentage;
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status;

    @OneToMany(mappedBy = "promotion")
    private Set<PaintVariant> paintVariants;


}
