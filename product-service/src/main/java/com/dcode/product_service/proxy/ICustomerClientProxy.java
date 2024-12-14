package com.dcode.product_service.proxy;

import com.dcode.product_service.config.AuthenticationRequestInterceptor;
import com.dcode.product_service.dto.user.UserResponse;
import com.dcode.product_service.dtoRequest.UserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@FeignClient(
        name = "identity-service",
        url = "${application.config.customer-url:http://localhost}",
        configuration = {AuthenticationRequestInterceptor.class}
)
public interface ICustomerClientProxy {
    @GetMapping("/api/v1/users/public/{customer-id}")
    Optional<UserResponse> findUserByUserId(@PathVariable("customer-id") String customerId);

    @PostMapping("/api/v1/users/reviews/info")
    List<UserResponse> findUserReviewInfos(List<UserRequest> userRequestList);
}
