package com.dcode.identity_service.dtorequest;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordRequest {
    @NotEmpty(message = "Old password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String oldPassword;
    @NotEmpty(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
    @NotEmpty(message = "Confirm password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String confirmPassword;
}
