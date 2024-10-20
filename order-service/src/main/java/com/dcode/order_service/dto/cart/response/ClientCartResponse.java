package com.dcode.order_service.dto.cart.response;

import lombok.Data;

import java.util.Set;

@Data
public class ClientCartResponse {
    private String cartId;
    private Set<CartVariantResponse> cartItems;
}
