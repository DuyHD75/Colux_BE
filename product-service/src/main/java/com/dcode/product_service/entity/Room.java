package com.dcode.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
public class Room extends Auditable{
    private String roomType;
    private String image;
    private String textUrl3D;

    @OneToMany(mappedBy = "room")
    private List<Collection> collections;
}
