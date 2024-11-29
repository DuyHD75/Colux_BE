package com.dcode.product_service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(
        name = "order-service",
        url = "${application.config.order-url:http://localhost}:8082"
)
public interface IOrderClientProxy {
    @GetMapping("/api/v1/orders/public/{customer-id}/{product-id}")
    Boolean hasCustomerPurchasedProduct(@PathVariable("customer-id") String customerId, @PathVariable("product-id") String productId);
}


