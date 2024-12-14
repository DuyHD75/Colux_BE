package com.dcode.identity_service.dtorequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
//@Accessors(fluent = true) getter and setter methods will be named in a fluent style
public class UserRequest {
    @NotEmpty(message = "First name is required")
    private String firstName;
    @NotEmpty(message = "Last name is required")
    private String lastName;
    @NotEmpty(message = "Email is required")
    @Email(message = "Email address is invalid")
    private String email;
    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    private String phone;

    @NotEmpty(message = "Role is required")
    private String role;
    private String bio;
}
