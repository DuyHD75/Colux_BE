package com.dcode.order_service.repository;

import com.dcode.order_service.entity.cart.CartVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ICartVariantRepository extends JpaRepository<CartVariantEntity, Long>, JpaSpecificationExecutor<CartVariantEntity> {
        void deleteByCart_CartIdAndVariantIdIn(String cartId, List<String> variantIds);
}
