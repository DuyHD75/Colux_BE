package com.dcode.order_service.resource;


import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.ConfirmedOrderResponse;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.exception.ResourceNotFoundException;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.service.IOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import static com.dcode.order_service.constant.Constants.AppConstants.FRONTEND_HOST;
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
    private final IOrderRepository orderRepository;

    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        return "Order service is up and running!";
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewOrder(@RequestBody @Valid OrderRequest orderRequest, HttpServletRequest request) {
        try {
            ConfirmedOrderResponse response = orderService.createClientOrder(orderRequest);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, "Order created successfully!", CREATED, Map.of("data", response))
            );
        } catch (BusinessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getData());
        }
    }

    @PutMapping("/cancel/{code}")
    public ResponseEntity<Response> cancelOrder(@PathVariable("code") String code, HttpServletRequest request) {
        orderService.cancelOrder(code);
        return ResponseEntity.ok().body(
                getResponse(request, "Order cancelled successfully!", OK, emptyMap())
        );
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

    @GetMapping("/{customer-id}/{product-id}")
    public boolean hasCustomerPurchasedProduct(@PathVariable("customer-id") String customerId, @PathVariable("product-id") String productId) {
        return orderService.hasCustomerPurchasedProduct(customerId, productId);
    }

    @GetMapping("/payment/success")
    public RedirectView captureTransactionPaypal(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, HttpServletRequest request) throws ResourceNotFoundException {
        try {
            orderService.captureTransactionPaypal(paymentId, payerId);
            return new RedirectView("https://colux.vercel.app/", true);
//            return new RedirectView(FRONTEND_HOST + "/payment/success", true);
        } catch (ResourceNotFoundException ex) {
            return new RedirectView("https://www.youtube.com/watch?v=_eTcseS410E&t=1552s", true);
        }
    }

    @GetMapping("/payment/cancel")
    public RedirectView cancelTransactionPaypal(@RequestParam("token") String token, HttpServletRequest request) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://colux.vercel.app/");
        return redirectView;
    }

    private URI getUri() {
        return URI.create("/api/v1/orders");
    }
}
