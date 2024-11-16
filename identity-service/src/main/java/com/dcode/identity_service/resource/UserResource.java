package com.dcode.identity_service.resource;


import com.dcode.identity_service.domain.Response;
import com.dcode.identity_service.domain.TokenData;
import com.dcode.identity_service.dto.User;
import com.dcode.identity_service.dtorequest.*;
import com.dcode.identity_service.exception.ApiException;
import com.dcode.identity_service.service.IJwtService;
import com.dcode.identity_service.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
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

    private static final Logger log = LoggerFactory.getLogger(UserResource.class);
    private final IUserService userService;
    private final Environment env;

    private final IJwtService jwtService;

    @GetMapping("/test")
    public String test() {
        String[] activeProfiles = env.getActiveProfiles();
        System.out.println("Active profiles: " + String.join(", ", activeProfiles));
        return "Hello World!";
    }

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {

        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getRole());

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

    @GetMapping("/refresh-token")
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

    @PostMapping("/change-password")
    public ResponseEntity<Response> changePassword(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid ChangePasswordRequest data) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated())
                throw new ApiException("User not authenticated.");

            var user = (User) authentication.getPrincipal();

            if (!data.getNewPassword().equals(data.getConfirmPassword())) {
                return ResponseEntity.status(BAD_REQUEST)
                        .body(getErrorResponse(request, response, new ApiException("Passwords do not match."), BAD_REQUEST));
            }

            if (data.getOldPassword().equals(data.getNewPassword())) {
                return ResponseEntity.status(BAD_REQUEST)
                        .body(getErrorResponse(request, response, new ApiException("New password cannot be the same as the old password."), BAD_REQUEST));
            }

            userService.changePassword(user.getEmail(), data.getOldPassword(), data.getNewPassword());

            return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Password changed.", HttpStatus.OK));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/password/reset")
    public ResponseEntity<Response> resetPassword(HttpServletRequest request, HttpServletResponse response, @RequestParam("email") String email) {
        try {
            userService.sendResetPasswordUri(email);
            return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Send reset password url successfully. Please check your email.", OK));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/password/reset/verify")
    public ResponseEntity<Response> verifyResetPasswordKey(@RequestParam("key") String key, HttpServletRequest request, HttpServletResponse response)  {
        try {
            userService.verifyResetPasswordKey(key);
            return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Verify reset password key successfully.", OK));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Response> resetPassword(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid ResetPasswordRequest data) {
        try {
            if (!data.getNewPassword().equals(data.getConfirmPassword())) {
                throw new ApiException("Passwords do not match.");
            }
            userService.resetPassword(data);
            return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Reset password successfully.", OK));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
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

    @GetMapping("/{customer-id}")
    public ResponseEntity<Response> getUserInfoById(@PathVariable("customer-id") String userId, HttpServletRequest request) {
        var user = userService.getUserByUserId(userId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "User info retrieved.", OK));
    }

    @GetMapping("/info")
    public ResponseEntity<Response> getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(getErrorResponse(request, response, new ApiException("User not authenticated."), UNAUTHORIZED));
        }
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "User info retrieved.", OK));
    }

    @PostMapping("/update-profile")
    public ResponseEntity<Response> updateUserProfile(@RequestBody @Valid UpdateProfileRequest user, HttpServletRequest request, HttpServletResponse response) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(getErrorResponse(request,response, new ApiException("User not authenticated."), UNAUTHORIZED));
        }

        var userEntity = (User) authentication.getPrincipal();
        userService.updateUserProfile(userEntity.getEmail(), user);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "User info updated.", OK));
    }

    @PostMapping("/reviews/info")
    public ResponseEntity<Response> getUserInfoForReview(@RequestBody @Valid List<UserReviewRequest> userReviewRequest, HttpServletRequest request, HttpServletResponse response){
        var userReviewResponse = userService.getUserReviewInfo(userReviewRequest);
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", userReviewResponse), "Users info retrieve success!", OK));
    }

    @GetMapping("/getTotalUser")
    public ResponseEntity<Response> getTotalUser(HttpServletRequest request) {
        var totalUser = userService.getTotalUser();
        return ResponseEntity.ok().body(getResponse(request, Map.of("totalUser", totalUser), "Total user retrieved.", OK));
    }

    @GetMapping("/monthlyUser")
    public ResponseEntity<Response> getMonthlyUserPageable(@RequestParam("monthBack") int monthBack,
                                                           HttpServletRequest request) {
        var monthlyUser = userService.getMonthlyUser(monthBack);
        return ResponseEntity.ok().body(getResponse(request, Map.of("monthlyUser", monthlyUser), "Monthly user retrieved.", OK));
    }

    private URI getUri() {
        return URI.create("");
    }
}





