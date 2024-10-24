package com.dcode.identity_service.repository;

import com.dcode.identity_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findUserByUserId(String userId);

    List<UserEntity> findAllByUserIdIn(List<String> customerIds);
}
