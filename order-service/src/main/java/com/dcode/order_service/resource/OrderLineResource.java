package com.dcode.order_service.resource;

import com.dcode.order_service.dto.order.response.OrderLineResponse;
import com.dcode.order_service.service.IOrderLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-lines")
@RequiredArgsConstructor
public class OrderLineResource {

    private final IOrderLineService service;

    @GetMapping("/order/{order-id}")
    public ResponseEntity<List<OrderLineResponse>> findByOrderId(
            @PathVariable("order-id") String orderId
    ) {
        return ResponseEntity.ok(service.findAllByOrderId(orderId));
    }

}
