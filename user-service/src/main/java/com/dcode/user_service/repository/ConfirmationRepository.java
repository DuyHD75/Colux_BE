package com.dcode.user_service.repository;

import com.dcode.user_service.entity.ConfirmationEntity;
import com.dcode.user_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {
    Optional<ConfirmationEntity> findByConfirmKey(String confirmKey);

    Optional<ConfirmationEntity> findByUserEntity(UserEntity userEntity);
}

