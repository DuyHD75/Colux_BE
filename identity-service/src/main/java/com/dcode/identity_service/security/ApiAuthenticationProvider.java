package com.dcode.identity_service.security;

import com.dcode.identity_service.domain.ApiAuthentication;
import com.dcode.identity_service.domain.UserPrincipal;
import com.dcode.identity_service.exception.ApiException;
import com.dcode.identity_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.dcode.identity_service.constant.Constants.AuthorityConstant.EXPIRATION_DAYS;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final IUserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var apiAuthentication = authenticationFunction.apply(authentication);

        var user = userService.getUserByEmail(apiAuthentication.getEmail());

        if (user != null) {
            var userCredential = userService.getUserCredentialById(user.getId());
            if (userCredential.getUpdatedAt().minusDays(EXPIRATION_DAYS).isAfter(LocalDateTime.now()))
                throw new ApiException("Account is expired. Please contact admin!");

            if (!user.isCredentialsNonExpired())
                throw new ApiException("Credential are expired. Please reset your password!");

            var userPrincipal = new UserPrincipal(user, userCredential);
            validAccount.accept(userPrincipal);

            if (passwordEncoder.matches(apiAuthentication.getPassword(), userCredential.getPassword())) {
                return ApiAuthentication.authenticated(user, userPrincipal.getAuthorities());
            } else throw new BadCredentialsException("Email and/or password incorrect. Please try again!");

        } else throw new ApiException("Unable to authentication!");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

    private final Function<Authentication, ApiAuthentication> authenticationFunction =
            (authentication) -> (ApiAuthentication) authentication;

    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
        if (!userPrincipal.isAccountNonExpired())
            throw new DisabledException("Account is expired, please contact admin!");
        if (!userPrincipal.isAccountNonLocked()) throw new LockedException("Account is locked");
        if (!userPrincipal.isCredentialsNonExpired())
            throw new CredentialsExpiredException("Password is expired, please reset your password!");
        if (!userPrincipal.isEnabled()) throw new DisabledException("Account is disabled!");
    };
}
