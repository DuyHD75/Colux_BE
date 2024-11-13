package com.dcode.product_service.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BrandRequest {
    @NotBlank(message = "Brand's name is required")
    private String name;
    @NotBlank(message = "Code is required")
    private String code;
    private String status;
}
