package com.dcode.order_service.proxy;

import com.dcode.order_service.dto.customer.CustomerResourceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "identity-service",
        url = "${application.config.customer-url"
)
public interface CustomerClientProxy {

    @GetMapping("/{customer-id")
    Optional<CustomerResourceResponse> findCustomerById(@PathVariable("customer-id") String customerId);


}
