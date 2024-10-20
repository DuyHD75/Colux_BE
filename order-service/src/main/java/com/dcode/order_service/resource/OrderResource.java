package com.dcode.order_service.resource;


import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.service.IOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.util.Collections.emptyMap;

import java.net.URI;
import java.util.Map;

import static com.dcode.order_service.utils.RequestUtils.getResponse;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderResource {
    private final IOrderService orderService;

    @GetMapping("/test")
    public String test(HttpServletRequest request) {
      return "Order service is up and running!";
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewOrder(@RequestBody @Valid OrderRequest orderRequest, HttpServletRequest request) {
        try {
            String approvalUrl = orderService.createClientOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(approvalUrl)).build();
        } catch (BusinessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getData());
        }
    }
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getData());
    }

    @GetMapping()
    public ResponseEntity<Response> getAllOrders(HttpServletRequest request) {

        var orders = orderService.getAllOrders();
        return ResponseEntity.ok().body(
                getResponse(request, "Orders retrieved successfully!", OK, Map.of("orders", orders))
        );
    }

    private URI getUri() {
        return URI.create("/api/v1/orders");
    }
//    private Response getResponse(HttpServletRequest request, String message, HttpStatus status, Map<String, Object> data) {
//        // Implement this method to return a Response object
//        return new Response(message, status, data);
//    }
}
