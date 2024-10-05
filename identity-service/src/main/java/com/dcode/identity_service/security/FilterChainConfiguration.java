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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.List;

import static com.dcode.identity_service.constant.Constants.AuthorityConstant.ALLOWED_PATHS;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class FilterChainConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FilterChainConfiguration.class);
    private final IUserService userService;
    private final IJwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
//                .cors(cors -> cors.disable())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new AuthenticationFilter(authenticationManager(), userService, jwtService),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthenticationFilter(jwtService), AuthenticationFilter.class)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(ALLOWED_PATHS)
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .logout(logout -> logout.logoutUrl("/api/v1/users/logout")
                        .addLogoutHandler(new CustomLogoutHandler(jwtService))
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            log.info("User logged out successfully");
                            SecurityContextHolder.clearContext();
                            response.setStatus(200);
                        }))
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        var apiAuthenticationProvider = new ApiAuthenticationProvider(userService, passwordEncoder);
        return new ProviderManager(List.of(apiAuthenticationProvider));
    }
}
