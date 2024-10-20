package com.dcode.order_service.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseResponseWrapper {
    private String path;
    private String message;
    private int status;
    private List<PurchaseResponse> data;
}