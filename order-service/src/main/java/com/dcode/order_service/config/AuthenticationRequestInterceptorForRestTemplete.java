package com.dcode.order_service.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@Slf4j
public class AuthenticationRequestInterceptorForRestTemplete implements ClientHttpRequestInterceptor {

    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName) ->
            Optional.ofNullable(request.getCookies())
                    .flatMap(cookies -> Arrays.stream(cookies)
                            .filter(cookie -> Objects.equals(cookie.getName(), cookieName))
                            .map(Cookie::getValue)
                            .findAny()
                    );

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest httpRequest = attributes.getRequest();

            Optional<String> accessToken = extractToken.apply(httpRequest, "access-token");
            Optional<String> refreshToken = extractToken.apply(httpRequest, "refresh-token");

            // Logging
            log.debug("Adding access token and refresh token to request cookies.");
            log.info("Access Token: {}", accessToken.orElse("null"));
            log.info("Refresh Token: {}", refreshToken.orElse("null"));

            // Add tokens to headers
            accessToken.ifPresent(token -> request.getHeaders().add("Cookie", "access-token=" + token));
            refreshToken.ifPresent(token -> request.getHeaders().add("Cookie", "refresh-token=" + token));
        }

        // Proceed with the request
        try {
            return execution.execute(request, body);
        } catch (IOException e) {
            log.error("Request execution failed", e);
            throw new RuntimeException(e); // Wrap IOException if needed
        }
    }

}
