package com.dcode.identity_service.resource;


import com.dcode.identity_service.domain.Response;
import com.dcode.identity_service.dtorequest.UserRequest;
import com.dcode.identity_service.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;

import static com.dcode.identity_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserResource {

    private final IUserService userService;

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

    @GetMapping("/info")
    public ResponseEntity<Response> getUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "User info retrieved.", OK));
    }

    private URI getUri() {
        return URI.create("");
    }
}





