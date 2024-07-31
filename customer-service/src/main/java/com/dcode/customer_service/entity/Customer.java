package com.dcode.customer_service.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class Customer {

    // create customer entity dont have the fisrt name last name email password because have the user-service for that
    // create the customer entity with the accumulated points, address,

    @Id
    private String id;
    private String accumulatedPoints;
    private Address address;
}
