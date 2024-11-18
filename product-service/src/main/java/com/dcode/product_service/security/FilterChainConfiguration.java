package com.dcode.product_service.security;


import com.dcode.product_service.service.IJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.dcode.product_service.constant.Constants.AppConstants.ALLOWED_PATHS;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class FilterChainConfiguration {

    private final IJwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(ALLOWED_PATHS)
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .build();
    }

}
