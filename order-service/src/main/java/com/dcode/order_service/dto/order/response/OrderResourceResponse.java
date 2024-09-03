package com.dcode.order_service.dto.order.response;

import com.dcode.order_service.dto.customer.CustomerResourceResponse;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class OrderResourceResponse {
    private Long id;
    private String code;
    private String name;
    private String color;
    @Nullable
    private CustomerResourceResponse customerResource;
    private Integer status;
}
