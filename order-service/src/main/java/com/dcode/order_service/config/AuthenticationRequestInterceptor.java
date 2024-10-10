package com.dcode.order_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import static java.util.Arrays.stream;

@Slf4j
public class AuthenticationRequestInterceptor implements RequestInterceptor {

    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName) ->
            Optional.ofNullable(request.getCookies())
                    .flatMap(cookies -> stream(cookies)
                            .filter(cookie -> Objects.equals(cookie.getName(), cookieName))
                            .map(Cookie::getValue)
                            .findAny()
                    );


    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        log.debug("Adding access token to request cookies.");
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // Extract tokens using the extractToken function
            Optional<String> accessToken = extractToken.apply(request, "access-token");
            Optional<String> refreshToken = extractToken.apply(request, "refresh-token");

            log.info("Access + Refresh: {} + {}", accessToken.orElse("null"), refreshToken.orElse("null"));

            // Add tokens to request headers if present
            accessToken.ifPresent(token -> requestTemplate.header("Cookie", "access-token=" + token));
            refreshToken.ifPresent(token -> requestTemplate.header("Cookie", "refresh-token=" + token));
        }
    }
}
