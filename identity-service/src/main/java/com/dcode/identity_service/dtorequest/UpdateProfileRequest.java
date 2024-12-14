package com.dcode.identity_service.dtorequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProfileRequest {
    @Size(min = 2, message = "First name must be at least 2 characters long.")
    private String firstName;

    @Size(min = 2, message = "Last name must be at least 2 characters long.")
    private String lastName;
    @Pattern(regexp = "^(\\+?[0-9]{1,4})?[0-9]{7,15}$",
            message = "Invalid phone number format. A valid number should be 7-15 digits, optionally with a country code.")
    private String phone;
    private String imageUrl;
}
