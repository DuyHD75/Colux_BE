package com.dcode.order_service.repository;

import com.dcode.order_service.entity.cart.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ICartRepository extends JpaRepository<CartEntity, Long>, JpaSpecificationExecutor<CartEntity> {

    Optional<CartEntity> findByCartId(String cartId);


    @Query("SELECT c FROM CartEntity c WHERE c.customerId = :customerId")
    Optional<CartEntity> findByCustomerId(String customerId);




}
