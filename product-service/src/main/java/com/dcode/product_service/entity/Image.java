package com.dcode.product_service.entity;

import com.dcode.product_service.enumeration.ImageType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image extends Auditable{
    @Column(nullable = false, updatable = false, unique = true)
    @NaturalId
    private String imageId;
    @Column(columnDefinition = "TEXT")
    private String url;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
