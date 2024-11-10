package com.dcode.product_service.entity;

import com.dcode.product_service.enumeration.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review extends Auditable{
    @Column(nullable = false, updatable = false, unique = true)
    private String reviewId;

    private String customerId;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer score;
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReviewStatus status;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Review parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Review> replies;
}
