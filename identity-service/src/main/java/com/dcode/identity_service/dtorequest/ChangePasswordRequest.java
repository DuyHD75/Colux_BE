package com.dcode.identity_service.dtorequest;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordRequest {
    @NotEmpty(message = "Old password is required")
    private String oldPassword;
    @NotEmpty(message = "New password is required")
    private String newPassword;
    @NotEmpty(message = "Confirm password is required")
    private String confirmPassword;
}
