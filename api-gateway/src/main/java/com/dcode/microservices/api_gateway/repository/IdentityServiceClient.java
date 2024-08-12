package com.dcode.microservices.api_gateway.repository;

import com.dcode.microservices.api_gateway.domain.Response;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityServiceClient {

    @PostExchange(value = "/api/v1/users/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<Response> introspectRequest(@RequestBody String token);

}

