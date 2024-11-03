package com.dcode.order_service.dto.cart.request;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CartVariantKeyRequest {
    private String cartId;
    private Map<String, List<String>> itemDeleteRequests; // key: variantId, value: list of cartItemId
}
