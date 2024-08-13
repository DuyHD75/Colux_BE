package com.dcode.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "paints")

public class Paint extends Product{

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private Product product;

    private String size;

    @OneToOne
    @JoinColumn(name = "color_id", referencedColumnName = "id")
    private Color color;

}
