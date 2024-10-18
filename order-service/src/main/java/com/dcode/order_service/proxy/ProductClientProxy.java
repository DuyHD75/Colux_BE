package com.dcode.order_service.proxy;

import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.dto.product.PurchaseResponse;
import com.dcode.order_service.dto.product.PurchaseResponseWrapper;
import com.dcode.order_service.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
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

        try {
            ResponseEntity<PurchaseResponseWrapper> responseEntity = restTemplate.exchange(
                    PRODUCT_URL + "/purchase-order",
                    POST,
                    requestEntity,
                    PurchaseResponseWrapper.class
            );

            if (responseEntity.getStatusCode().isError()) {
                throw new BusinessException("Error while purchasing products :: " + responseEntity.getStatusCode());
            }
            return responseEntity.getBody().getData();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR || e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                // Xử lý lỗi 500 và 400 và trả về thông tin chi tiết về các sản phẩm không thành công
                try {
                    PurchaseResponseWrapper errorResponse = new ObjectMapper().readValue(e.getResponseBodyAsString(), PurchaseResponseWrapper.class);
                    return errorResponse.getData();
                } catch (JsonProcessingException jsonException) {
                    throw new BusinessException("Error while parsing error response from product service", jsonException);
                }
            } else {
                throw new BusinessException("Error while purchasing products :: " + e.getStatusCode());
            }
        } catch (Exception e) {
            throw new BusinessException("Unexpected error while purchasing products", e);
        }
    }
}