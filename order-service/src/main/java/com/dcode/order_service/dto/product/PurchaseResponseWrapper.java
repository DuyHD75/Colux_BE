package com.dcode.order_service.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseResponseWrapper {
    private List<PurchaseResponse> data;

}