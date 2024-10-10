package com.dcode.order_service.repository;

import com.dcode.order_service.entity.cart.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ICartRepository extends JpaRepository<CartEntity, Long>, JpaSpecificationExecutor<CartEntity> {
/*
    @Query("SELECT c FROM CartEntity c JOIN c.user u WHERE u.username = :username AND c.status = 1")
    Optional<CartEntity> findByUsername(@Param("username") String username);*/
}
