package com.dcode.identity_service.repository;

import com.dcode.identity_service.entity.ConfirmationEntity;
import com.dcode.identity_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {
    Optional<ConfirmationEntity> findByConfirmKey(String confirmKey);

    Optional<ConfirmationEntity> findByUserEntity(UserEntity userEntity);
}

