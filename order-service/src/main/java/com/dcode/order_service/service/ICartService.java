package com.dcode.order_service.service;

import com.dcode.order_service.dto.cart.request.CartRequest;
import com.dcode.order_service.entity.cart.CartEntity;

import java.util.List;

public interface ICartService {
    void createClientCart(CartRequest request);

    List<CartEntity> getAllCarts();
}
