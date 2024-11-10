package com.dcode.order_service.service;

import com.dcode.order_service.dto.cart.request.CartRequest;
import com.dcode.order_service.dto.cart.request.CartVariantKeyRequest;
import com.dcode.order_service.dto.cart.response.ClientCartResponse;

public interface ICartService {
    ClientCartResponse saveClientCart(CartRequest request);

    ClientCartResponse getCart(String customerId);

    void deleteCartItem(CartVariantKeyRequest idRequests);
}
