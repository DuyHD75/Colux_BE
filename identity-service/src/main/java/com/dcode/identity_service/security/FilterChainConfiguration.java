package com.dcode.identity_service.security;

import com.dcode.identity_service.service.IJwtService;
import com.dcode.identity_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class FilterChainConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FilterChainConfiguration.class);
    private final IUserService userService;
    private final IJwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;


    private static final List<String> ALLOWED_PATHS = List.of(
            "/api/v1/users/register",
            "/api/v1/users/verify/account");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new AuthenticationFilter(authenticationManager(), userService, jwtService),
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(ALLOWED_PATHS.toArray(new String[0]))
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        var apiAuthenticationProvider = new ApiAuthenticationProvider(userService, passwordEncoder);
        return new ProviderManager(List.of(apiAuthenticationProvider));
    }
}
