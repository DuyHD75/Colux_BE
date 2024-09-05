package com.dcode.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ColorFamily extends Auditable {
    private String name;
    private String description;

    @OneToMany(mappedBy = "colorFamily")
    private List<Collection> collections;
}
