package com.dcode.product_service.dtoRequest.order_service;

import lombok.Data;
import java.util.List;

@Data
public class RestoreMessageDTO {
    private List<OrderLineDTO> orderLines;
}
