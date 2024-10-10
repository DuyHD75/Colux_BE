package com.dcode.microservices.api_gateway.service;


import com.dcode.microservices.api_gateway.domain.Response;
import com.dcode.microservices.api_gateway.repository.IdentityServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IdentityService {
    private final IdentityServiceClient identityServiceClient;

    public Mono<Response> introspectRequest(String token) {
        return identityServiceClient.introspectRequest(token);
    }
}
