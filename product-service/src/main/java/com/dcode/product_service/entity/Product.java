package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
//@Inheritance(strategy = InheritanceType.JOINED)
public class Product extends Auditable{

    @Column(updatable = false, nullable = false, unique = true)
    private String productId;
    private String productName;
    private String description;
    private String price;
    private String ratingAverage;
    private String placeOfOrigin;
    private String warranty;

    @OneToMany(mappedBy = "product")
    private List<ProductPriceTracking> productPriceTrackings;

    @ManyToMany
    @JoinTable(
            name = "product_property_value",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "property_value_id", referencedColumnName = "id")
    )
    private List<PropertyValue> propertyValues;

    @ManyToMany
    @JoinTable(
            name = "product_feature_value",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "feature_value_id", referencedColumnName = "id")
    )
    private List<FeatureValue> featureValues;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
//    @JsonIgnoreProperties("products")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
//    @JsonIgnoreProperties("products")
    private Brand brand;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product")
    private List<Paint> paints;

    @OneToMany(mappedBy = "product")
    private List<Wallpaper> wallpapers;

    @OneToMany(mappedBy = "product")
    private List<Floor> floors;
}
