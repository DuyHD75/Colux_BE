package com.dcode.microservices.api_gateway.configuration;

import com.dcode.microservices.api_gateway.repository.IdentityServiceClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static com.dcode.microservices.api_gateway.constant.Constants.AuthorityConstant.ALLOWED_PATHS;
import static com.dcode.microservices.api_gateway.utils.RequestUtils.getErrorResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class GlobalAuthenticationFilter implements GlobalFilter, Ordered {

    private final IdentityServiceClient identityServiceClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String requestURI = exchange.getRequest().getURI().getPath();

        if (isAllowedPath.apply(requestURI)) {
            return chain.filter(exchange);
        }

        var accessToken = exchange.getRequest().getCookies().getFirst("access-token");

        if (accessToken == null) {
            try {
                return unAuthenticatedError(exchange, "Access token not found");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return identityServiceClient.introspectRequest(accessToken.getValue())
                .flatMap(response -> {
                    Map<String, Object> tokenData = (Map<String, Object>) response.data().get("tokenData");
                    Boolean validToken = (Boolean) tokenData.get("validToken");

                    if (Boolean.TRUE.equals(validToken)) {
                        return chain.filter(exchange);
                    }
                    try {
                        return unAuthenticatedError(exchange, "Invalid token");
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).onErrorResume(throwable -> {
                    try {
                        return unAuthenticatedError(exchange, "Something went wrong in the authentication process!");
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> unAuthenticatedError(ServerWebExchange exchange, String error) throws JsonProcessingException {
        return getErrorResponse(
                exchange.getRequest(),
                exchange.getResponse(),
                new AccessDeniedException(error),
                HttpStatus.UNAUTHORIZED
        );
    }

    private static Function<String, Boolean> isAllowedPath = requestURI -> Arrays.stream(ALLOWED_PATHS).anyMatch(requestURI::matches);
}
