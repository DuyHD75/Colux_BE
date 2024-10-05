package com.dcode.product_service.dtoRequest;

import com.dcode.product_service.entity.OrderRequest;
import lombok.Data;

@Data
public class ProductOrderRequest implements OrderRequest {
    private String productId;
    private String variantId;
    private String colorId;
    private Double quantity;
    private boolean success;

    public String getIdentity(){
        return colorId;
    }
}
