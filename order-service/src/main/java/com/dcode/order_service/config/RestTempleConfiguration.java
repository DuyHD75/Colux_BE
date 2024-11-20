package com.dcode.order_service.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class RestTempleConfiguration {
    private final AuthenticationRequestInterceptorForRestTemplete authenticationRequestInterceptor;

    public RestTempleConfiguration(AuthenticationRequestInterceptorForRestTemplete authenticationRequestInterceptor) {
        this.authenticationRequestInterceptor = authenticationRequestInterceptor;
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .additionalInterceptors(authenticationRequestInterceptor)
                .build();
    }
}

