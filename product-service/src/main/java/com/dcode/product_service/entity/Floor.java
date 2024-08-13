package com.dcode.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "floors")
public class Floor extends Product{
    private Integer width;
    private Integer height;
    private Integer thickness;
    private String material;
    private String color;
}
