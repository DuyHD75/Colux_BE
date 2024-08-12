package com.dcode.identity_service.resource;


import com.dcode.identity_service.domain.Response;
import com.dcode.identity_service.domain.TokenData;
import com.dcode.identity_service.dtorequest.UserRequest;
import com.dcode.identity_service.exception.ApiException;
import com.dcode.identity_service.service.IJwtService;
import com.dcode.identity_service.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.identity_service.enumeration.TokenType.ACCESS_TOKEN;
import static com.dcode.identity_service.enumeration.TokenType.REFRESH_TOKEN;
import static com.dcode.identity_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.identity_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserResource {

    private final IUserService userService;

    private final IJwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {

        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());

        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(),
                        "Account created successfully! Check your email to enable your account.", CREATED)
        );
    }

    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifyAccount(@RequestParam("key") String key, HttpServletRequest request) {
        userService.verifyAccountKey(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account verified.", OK));
    }

    @GetMapping("/refresh_token")
    public ResponseEntity<Response> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtService.extractToken(request, "refresh-token").get();

        TokenData tokenData = jwtService.getTokenData(token, data -> data);

        if (!tokenData.isValidToken()) {
            return ResponseEntity.created(getUri())
                    .body(getResponse(request, emptyMap(), "Invalid token.", UNAUTHORIZED));
        }

        jwtService.addCookie(response, tokenData.getUser(), ACCESS_TOKEN);
        jwtService.addCookie(response, tokenData.getUser(), REFRESH_TOKEN);

        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Token refreshed.", OK));
    }

    @PostMapping("/introspect")
    public ResponseEntity<Response> introspect(HttpServletRequest request, HttpServletResponse response, @RequestBody String token) {

        if (token == null) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(getErrorResponse(request, response, new ApiException("Token not found."), UNAUTHORIZED));
        }

        TokenData tokenData = jwtService.getTokenData(token, data -> data);

        if (!tokenData.isValidToken()) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(getErrorResponse(request, response, new ApiException("Invalid token."), UNAUTHORIZED));
        }

        return ResponseEntity.ok().body(getResponse(request, Map.of("tokenData", tokenData), "Token introspected.", OK));
    }

    @GetMapping("/info")
    public ResponseEntity<Response> getUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "User info retrieved.", OK));
    }

    private URI getUri() {
        return URI.create("");
    }
}





