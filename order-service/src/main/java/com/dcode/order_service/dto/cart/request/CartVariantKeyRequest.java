package com.dcode.order_service.dto.cart.request;


import lombok.Data;

import java.util.List;

@Data
public class CartVariantKeyRequest {
    private String cartId;
    private List<String> variantIds;
}
