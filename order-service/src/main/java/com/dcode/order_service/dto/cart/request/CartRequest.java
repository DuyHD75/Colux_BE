package com.dcode.order_service.dto.cart.request;

import com.dcode.order_service.dto.cart.UpdateQuantityType;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
public class CartRequest {
    @Nullable
    private String cartId;
    private String customerId;
    private Integer status;
    private List<CartVariantRequest> cartItems;
    private UpdateQuantityType updateQuantityType;
}