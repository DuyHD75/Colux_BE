package com.dcode.identity_service.entity;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.OnDelete;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static jakarta.persistence.FetchType.EAGER;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "confirmations")
@JsonInclude(NON_DEFAULT)

public class ConfirmationEntity extends Auditable {

    private String confirmKey;

    @OneToOne(targetEntity = UserEntity.class, fetch = EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private UserEntity userEntity;

    public ConfirmationEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.confirmKey = UUID.randomUUID().toString();
    }
}
