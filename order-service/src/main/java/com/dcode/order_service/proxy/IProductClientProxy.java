package com.dcode.order_service.proxy;

import com.dcode.order_service.config.AuthenticationRequestInterceptor;
import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.cart.request.CartVariantRequest;
import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.dto.product.OrderLineDTO;
import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.dto.product.PurchaseResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@FeignClient(
        name = "product-service",
        url = "${application.config.product-url}",
        configuration = {AuthenticationRequestInterceptor.class}
)
public interface IProductClientProxy {
    @PostMapping("/getInfo")
    Optional<Response> findProductInfo(@RequestBody List<CartVariantRequest> productOrderRequests);

    @GetMapping("/reduceProduct")
    Optional<Response> reduceProductQuantity(@RequestBody List<OrderLineDTO> productOrderRequests);

    @GetMapping("/getProductDashboard")
    Optional<Response> getProductDashboard(List<CartVariantRequest> productDashboardRequests);

    @GetMapping("/getDashboardInfo")
    Optional<Response> getDashboardInfo();

    @PostMapping("/getProductByVariant")
    Optional<Response> getProductByVariantId(@RequestBody List<CartVariantRequest> cartVariantRequests);

    @PostMapping("/purchase-order")
    Optional<Response> purchaseProducts(@RequestBody List<PurchaseRequest> purchaseRequests);
}
