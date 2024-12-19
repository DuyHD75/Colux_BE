package com.dcode.product_service.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ColorFamilyRequest {
    @NotBlank(message = "Name is required.")
    private String name;

    private String title;

    private String description;

    @NotBlank(message = "Hex is required.")
    private String hex;

    private String image;

    private Set<String> collections;
}
