package com.dcode.order_service.proxy;

import com.dcode.order_service.config.AuthenticationRequestInterceptor;
import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.dashboard.request.UserRequest;
import com.dcode.order_service.dto.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@FeignClient(
        name = "identity-service",
        url = "${application.config.customer-url}",
        configuration = {AuthenticationRequestInterceptor.class}
)
public interface ICustomerClientProxy {
    @GetMapping("/public/{customer-id}")
    Optional<Response> findUserByUserId(@PathVariable("customer-id") String customerId);

    @GetMapping("/getTotalUser")
    Optional<Response> getTotalUser();

    @GetMapping("/monthlyUser")
    Optional<Response> monthlyUser(@RequestParam("monthBack") int monthBack);
}

