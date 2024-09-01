package com.dcode.product_service.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Name can't empty")
    private String name;
    private String thumbnail;

}
