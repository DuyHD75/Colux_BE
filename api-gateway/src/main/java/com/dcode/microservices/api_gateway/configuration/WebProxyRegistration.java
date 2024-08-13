package com.dcode.microservices.api_gateway.configuration;


import com.dcode.microservices.api_gateway.repository.IdentityServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebProxyRegistration {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8100")
                .build();
    }

    @Bean
    public IdentityServiceClient identityServiceClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(
                WebClientAdapter.create(webClient)
        ).build();

        return factory.createClient(IdentityServiceClient.class);
    }


}
