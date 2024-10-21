package com.dcode.order_service.proxy;

import com.dcode.order_service.config.AuthenticationRequestInterceptor;
import com.dcode.order_service.dto.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "identity-service",
        url = "${application.config.customer-url:http://localhost}:8100",
        configuration = {AuthenticationRequestInterceptor.class}
)
public interface ICustomerClientProxy {
    @GetMapping("/api/v1/users/{customer-id}")
    Optional<UserResponse> findUserByUserId(@PathVariable("customer-id") String customerId);
}
