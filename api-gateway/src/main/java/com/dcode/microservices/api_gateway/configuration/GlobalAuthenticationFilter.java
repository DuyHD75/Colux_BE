package com.dcode.microservices.api_gateway.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class GlobalAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // get token from header
        List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        log.info("Token: {}", authHeaders);
        if (CollectionUtils.isEmpty(authHeaders)) return unAuthenticatedError(exchange, "Invalid token!");
        String token = authHeaders.get(0).replace("Bearer ", "");
        log.info("Token: {}", token);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    Mono<Void> unAuthenticatedError(ServerWebExchange exchange, String error) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(error.getBytes())));

    }
}
