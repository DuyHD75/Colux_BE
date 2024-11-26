package com.dcode.product_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "productName"),
        @Index(name = "idx_fulltext", columnList = "productName, description", unique = true)
})
//@Inheritance(strategy = InheritanceType.JOINED)
public class Product extends Auditable{

    @Column(updatable = false, nullable = false, unique = true)
    @NaturalId
    private String productId;
    private String productName;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Min(0)
    @Max(5)
    private Double ratingAverage;
    private String code;
    private String placeOfOrigin;
    private String warranty;
    private String applicableSurface;


    @ManyToMany
    @JoinTable(
            name = "product_property_value",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "property_value_id", referencedColumnName = "id")
    )
    private Set<PropertyValue> propertyValues;

    @ManyToMany
    @JoinTable(
            name = "product_feature_value",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "feature_value_id", referencedColumnName = "id")
    )
    private Set<FeatureValue> featureValues;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
//    @JsonIgnoreProperties("products")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
//    @JsonIgnoreProperties("products")
    private Brand brand;

    @OneToMany(mappedBy = "product")
    private List<Paint> paints;

    @OneToMany(mappedBy = "product")
    private List<Wallpaper> wallpapers;

    @OneToMany(mappedBy = "product")
    private List<Floor> floors;

    @OneToMany(mappedBy = "product")
    private Set<Image> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> review;

    @ManyToOne
    @JoinColumn(name = "product_supplier_id", referencedColumnName = "id")
    private ProductSupplier productSupplier;

    @ManyToOne
    @JoinColumn(name = "wish_list_id", referencedColumnName = "id")
    private WishList wishList;
}
