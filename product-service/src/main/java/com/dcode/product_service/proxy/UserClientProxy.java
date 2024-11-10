package com.dcode.product_service.proxy;


import com.dcode.product_service.dto.user.UserResponse;
import com.dcode.product_service.dto.user.UserResponseWrapper;
import com.dcode.product_service.dtoRequest.UserRequest;
import com.dcode.product_service.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserClientProxy {

    @Value("${application.config.customer-url}")
    private String IDENTITY_URL;

    private final RestTemplate restTemplate;

    public List<UserResponse> findUserReviewInfos(List<UserRequest> customerIdRequests) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<List<UserRequest>> requestEntity = new HttpEntity<>(customerIdRequests, headers);

        try {
            ResponseEntity<UserResponseWrapper> responseEntity = restTemplate.exchange(
                    IDENTITY_URL + "/api/v1/users/reviews/info",
                    HttpMethod.POST,
                    requestEntity,
                    UserResponseWrapper.class
            );

            UserResponseWrapper responseWrapper = responseEntity.getBody();
            if (responseWrapper == null || responseWrapper.getData() == null) {
                throw new BusinessException("No user information found in response");
            }

            return responseWrapper.getData().getUser();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            try {
                UserResponseWrapper errorResponse = new ObjectMapper().readValue(e.getResponseBodyAsString(), UserResponseWrapper.class);
                throw new BusinessException("Error response from identity service: " + errorResponse);
            } catch (JsonProcessingException jsonException) {
                throw new BusinessException("Error while parsing error response from identity service");
            }
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            throw new BusinessException("Unexpected error while fetching user information");
        }
    }
}