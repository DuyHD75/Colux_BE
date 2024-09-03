package com.dcode.order_service.resource;

import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.service.IOrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderResource {

    private final IOrderService orderService;


    @PostMapping
    public ResponseEntity<Integer> createNewOrder(
            @RequestBody @Valid OrderRequest request
    ){
        return ResponseEntity.ok(orderService.createNewOrder(request));
    }



}
