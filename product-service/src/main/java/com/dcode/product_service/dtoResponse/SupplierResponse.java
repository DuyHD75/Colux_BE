package com.dcode.product_service.dtoResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierResponse {
    @JsonProperty("id")
    private String supplierId;
    private String name;
    private String code;
    private String phone;
    private String email;
}
