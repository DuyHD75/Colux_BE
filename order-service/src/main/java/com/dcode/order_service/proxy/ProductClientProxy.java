package com.dcode.order_service.proxy;

import com.dcode.order_service.dto.cart.request.CartVariantRequest;
import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.dto.product.PurchaseResponse;
import com.dcode.order_service.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
public class ProductClientProxy {

    @Value("${application.config.product-url}")
    private String PRODUCT_URL;

    private final RestTemplate restTemplate;

    public List<PurchaseResponse> purchaseProducts(List<PurchaseRequest> purchaseRequests) {
        HttpHeaders headers = new HttpHeaders();

        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<List<PurchaseRequest>> requestEntity = new HttpEntity<>(purchaseRequests, headers);

        ParameterizedTypeReference<List<PurchaseResponse>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<PurchaseResponse>> responseEntity = restTemplate.exchange(
                PRODUCT_URL + "/purchase-order",
                POST,
                requestEntity,
                responseType
        );

        if (responseEntity.getStatusCode().isError()) {
            throw new BusinessException("Error while purchasing products :: " + responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

    public List<CartVariantResponse.ClientVariantResponse> getProductByVariantId(List<CartVariantRequest> cartVariantRequests) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<List<CartVariantRequest>> requestEntity = new HttpEntity<>(cartVariantRequests, headers);

        ParameterizedTypeReference<List<CartVariantResponse.ClientVariantResponse>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<List<CartVariantResponse.ClientVariantResponse>> responseEntity = restTemplate.exchange(
                PRODUCT_URL + "/getProductByVariant/",
                HttpMethod.POST,
                requestEntity,
                responseType
        );

        if (responseEntity.getStatusCode().isError()) {
            throw new BusinessException("Error while fetching variant :: " + responseEntity.getStatusCode());
        }

        return responseEntity.getBody();
    }







}
