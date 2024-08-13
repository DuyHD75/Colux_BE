package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
public class Product extends Auditable{

    @Column(updatable = false, nullable = false, unique = true)
    private String productName;
    private String description;
    private String price;
    private String ratingAverage;
    private String category;
    private String detail;
    private String placeOfOrigin;
    private String warranty;

}
