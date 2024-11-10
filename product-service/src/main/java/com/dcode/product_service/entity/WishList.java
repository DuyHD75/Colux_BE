package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wish_list", uniqueConstraints = @UniqueConstraint(columnNames = "customerId"))
public class WishList extends Auditable{

    @Column(nullable = false, updatable = false, unique = true)
    private String wishListId;

    @Column(nullable = false, unique = true)
    private String customerId;

    @OneToMany(mappedBy = "wishList", cascade = CascadeType.ALL )
    private Set<Product> products;


}
