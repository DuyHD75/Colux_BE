package com.dcode.order_service.proxy;

import com.dcode.order_service.dto.cart.request.CartVariantRequest;
import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.dto.product.PurchaseResponse;
import com.dcode.order_service.dto.product.PurchaseResponseWrapper;
import com.dcode.order_service.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductClientProxy {

    @Value("${application.config.product-url}")
    private String PRODUCT_URL;

    private final RestTemplate restTemplate;

    public PurchaseResponseWrapper purchaseProducts(List<PurchaseRequest> purchaseRequests) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<List<PurchaseRequest>> requestEntity = new HttpEntity<>(purchaseRequests, headers);

        try {
            ResponseEntity<PurchaseResponseWrapper> responseEntity = restTemplate.exchange(
                    PRODUCT_URL + "/purchase-order",
                    POST,
                    requestEntity,
                    PurchaseResponseWrapper.class
            );

            PurchaseResponseWrapper responseWrapper = responseEntity.getBody();
            assert responseWrapper != null;
            responseWrapper.setStatus(responseEntity.getStatusCode().value());
            return responseWrapper;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            try {
                PurchaseResponseWrapper errorResponse = new ObjectMapper().readValue(e.getResponseBodyAsString(), PurchaseResponseWrapper.class);
                errorResponse.setStatus(e.getStatusCode().value());
                return errorResponse;
            } catch (JsonProcessingException jsonException) {
                throw new BusinessException("Error while parsing error response from product service");
            }
        } catch (Exception e) {
            log.error("here: ", e);
            throw new BusinessException("Unexpected error while purchasing products");
        }
    }

    public List<CartVariantResponse.ClientVariantResponse> getProductByVariantId(List<CartVariantRequest> cartVariantRequests) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<List<CartVariantRequest>> requestEntity = new HttpEntity<>(cartVariantRequests, headers);

        ParameterizedTypeReference<List<CartVariantResponse.ClientVariantResponse>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<List<CartVariantResponse.ClientVariantResponse>> responseEntity = restTemplate.exchange(
                PRODUCT_URL + "/getProductByVariant",
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
