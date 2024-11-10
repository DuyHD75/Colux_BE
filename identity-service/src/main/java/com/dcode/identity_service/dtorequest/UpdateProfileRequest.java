package com.dcode.identity_service.dtorequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String imageUrl;
}
