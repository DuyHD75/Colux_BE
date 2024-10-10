package com.dcode.identity_service.security;

import com.dcode.identity_service.domain.Response;
import com.dcode.identity_service.dto.User;
import com.dcode.identity_service.dtorequest.LoginRequest;
import com.dcode.identity_service.service.IJwtService;
import com.dcode.identity_service.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

import static com.dcode.identity_service.constant.Constants.AuthorityConstant.LOGIN_PATH;
import static com.dcode.identity_service.domain.ApiAuthentication.unauthenticated;
import static com.dcode.identity_service.enumeration.LoginType.*;
import static com.dcode.identity_service.enumeration.TokenType.*;
import static com.dcode.identity_service.utils.RequestUtils.*;
import static com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final IUserService userService;
    private final IJwtService jwtService;

    protected AuthenticationFilter(AuthenticationManager authenticationManager,
                                   IUserService userService,
                                   IJwtService jwtService) {
        super(new AntPathRequestMatcher(LOGIN_PATH, POST.name()), authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            var user = new ObjectMapper().configure(AUTO_CLOSE_SOURCE, true)
                    .readValue(request.getInputStream(), LoginRequest.class);

            userService.updateLoginAttempt(user.getEmail(), LOGIN_ATTEMPT);

            var authentication = unauthenticated(user.getEmail(), user.getPassword()); // return ApiAuthentication [Authentication]

            return getAuthenticationManager().authenticate(authentication);

        } catch (Exception exception) {
            log.error("Error while authenticating user", exception.getMessage());
            writeErrorResponse(request, response,exception, UNAUTHORIZED);
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        var user = (User) authentication.getPrincipal();
        userService.updateLoginAttempt(user.getEmail(), LOGIN_SUCCESS);

        var httpResponse = user.isMfa() ? sendQrCode(request, user) : sendResponse(request, response, user);

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(OK.value());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var out = response.getOutputStream();
        new ObjectMapper().writeValue(out, httpResponse);
        out.flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        writeErrorResponse(request, response, failed, UNAUTHORIZED);
        SecurityContextHolder.clearContext();

    }
    private Response sendResponse(HttpServletRequest request, HttpServletResponse response, User user) {
        jwtService.addCookie(response, user, ACCESS_TOKEN);
        jwtService.addCookie(response, user, REFRESH_TOKEN);
        return getResponse(request, Map.of("user", user), "Login success !", OK);
    }

    private Response sendQrCode(HttpServletRequest request, User user) {
        return getResponse(request, Map.of("user", user), "Scan the QR code to login", OK);
    }
}
