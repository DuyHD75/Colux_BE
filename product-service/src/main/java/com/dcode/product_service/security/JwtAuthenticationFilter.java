package com.dcode.product_service.security;

import com.dcode.product_service.domain.TokenData;
import com.dcode.product_service.service.IJwtService;
import com.dcode.product_service.domain.RequestContext;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import static com.dcode.product_service.constant.Constants.AppConstants.ALLOWED_PATHS;
import static com.dcode.product_service.utils.RequestUtils.writeErrorResponse;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {

            String requestURI = request.getRequestURI();

            if (isAllowedPath.apply(requestURI)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = jwtService.extractToken(request, "access-token")
                    .orElseThrow(() -> new JwtException("Token not found !"));

            if (token.isBlank()) {
                writeErrorResponse(request, response, new JwtException("Token not found !"), UNAUTHORIZED);
                return;
            }

            TokenData tokenData = jwtService.getTokenData(token, data -> data);
            if (!tokenData.isValidToken()) {
                writeErrorResponse(request, response, new JwtException("Invalid token !"), UNAUTHORIZED);
                return;
            }

            var authentication = new UsernamePasswordAuthenticationToken(
                    tokenData.getUserId(), "PASSWORD_PROTECTED", tokenData.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            RequestContext.setUserId(tokenData.getUserId());

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Error JWT Authentication: ", ex.getMessage());
            writeErrorResponse(request, response, ex, UNAUTHORIZED);
        }
    }

    private final Function<String, Boolean> isAllowedPath = requestURI ->
            Arrays.stream(ALLOWED_PATHS).anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
}