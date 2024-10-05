package com.dcode.order_service.dto.cart.request;

import com.dcode.order_service.dto.cart.UpdateQuantityType;
import lombok.Data;

import java.util.Set;

@Data
public class CartRequest {
    private String cartId;
    private String userId;
    private Set<CartVariantRequest> cartItems;
    private Integer status;
    private UpdateQuantityType updateQuantityType;
}
