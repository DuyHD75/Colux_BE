package com.dcode.order_service.proxy;

import com.dcode.order_service.config.AuthenticationRequestInterceptor;
import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.cart.request.CartVariantRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@FeignClient(
        name = "product-service",
        url = "${application.config.product-url}"
//        configuration = {AuthenticationRequestInterceptor.class}
)
public interface IProductClientProxy {
    @GetMapping("/getInfo")
    Optional<Response> findProductInfo(@RequestBody List<CartVariantRequest> productOrderRequests);
}
