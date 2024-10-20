package com.dcode.order_service.service;

import com.dcode.order_service.dto.cart.request.CartRequest;
import com.dcode.order_service.dto.cart.request.CartVariantKeyRequest;
import com.dcode.order_service.dto.cart.response.ClientCartResponse;
import com.dcode.order_service.entity.cart.CartEntity;

import java.util.List;

public interface ICartService {
    ClientCartResponse createClientCart(CartRequest request);

    ClientCartResponse getCart(String customerId);

    void deleteCartItem(CartVariantKeyRequest idRequests);
}
