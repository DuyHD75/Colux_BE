package com.dcode.microservices.api_gateway.configuration;


import com.dcode.microservices.api_gateway.repository.IdentityServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@Configuration
public class WebProxyRegistration {
    @Value("${identity.service.url}")
    private String IDENTITY_SERVICE_URL;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(IDENTITY_SERVICE_URL)
                .build();
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("https://colux.vercel.app", "http://localhost:3000", "http://localhost:3001"));
        corsConfiguration.setAllowedHeaders(List.of("Content-Type"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    public IdentityServiceClient identityServiceClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(
                WebClientAdapter.create(webClient)
        ).build();
        return factory.createClient(IdentityServiceClient.class);
    }
}
