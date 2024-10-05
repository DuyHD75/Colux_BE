package com.dcode.product_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
public class Room extends Auditable{

    @Column(updatable = false, unique = true, nullable = false)
    @NaturalId
    private String roomId;
    private String roomType;
    private String hex;
    private String title;
    private String description;
    private String image;
    private String textUrl3D;

    @OneToMany(mappedBy = "room")
    private List<Collection> collections;
}
