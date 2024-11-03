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
@CrossOrigin(FRONTEND_HOST)
public class OrderResource {
    private final IOrderService orderService;
    private final IOrderRepository orderRepository;


    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        try {
            return "Order service is up and running!";
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createNewOrder(@RequestBody @Valid OrderRequest orderRequest, HttpServletRequest request) {
        try {
            ConfirmedOrderResponse response = orderService.createClientOrder(orderRequest);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, "Order created successfully!", CREATED, Map.of("data", response))
            );
        } catch (BusinessException ex) {
            return ResponseEntity.internalServerError().body(getResponse(request, "Error: " + ex.getMessage(), INTERNAL_SERVER_ERROR, Map.of("errorData", ex.getData())));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(getResponse(request, "Error: " + ex.getMessage(), INTERNAL_SERVER_ERROR, emptyMap()));
        }
    }

    @PutMapping("/cancel/{code}")
    public ResponseEntity<Response> cancelOrder(@PathVariable("code") String code, HttpServletRequest request) {
        try {
            orderService.cancelOrder(code);
            return ResponseEntity.ok().body(
                    getResponse(request, "Order cancelled successfully!", OK, emptyMap())
            );
        }
        catch (BusinessException ex) {
            return ResponseEntity.internalServerError().body(getResponse(request, "Error: " + ex.getMessage(), INTERNAL_SERVER_ERROR, Map.of("errorData", ex.getData())));
        }
        catch (Exception ex) {
            return ResponseEntity.internalServerError().body(getResponse(request, "Error: " + ex.getMessage(), INTERNAL_SERVER_ERROR, emptyMap()));
        }
    }

    @GetMapping()
    public ResponseEntity<Response> getAllOrders(HttpServletRequest request) {
        try {
            var orders = orderService.getAllOrders();
            return ResponseEntity.ok().body(
                    getResponse(request, "Orders retrieved successfully!", OK, Map.of("orders", orders))
            );
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(getResponse(request, "Error: " + ex.getMessage(), INTERNAL_SERVER_ERROR, emptyMap()));
        }
    }

    @GetMapping("/{customer-id}/{product-id}")
    public boolean hasCustomerPurchasedProduct(@PathVariable("customer-id") String customerId, @PathVariable("product-id") String productId) {
        try {
            return orderService.hasCustomerPurchasedProduct(customerId, productId);
        } catch (Exception ex) {
            return false;
        }
    }

    @GetMapping("/payment/success")
    public ResponseEntity<Void> captureTransactionPaypal(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, HttpServletRequest request) {
        try {
            orderService.captureTransactionPaypal(paymentId, payerId);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://colux.vercel.app/")).build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://www.youtube.com/watch?v=_eTcseS410E&t=1552s")).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://colux.vercel.app/")).build();
        }
    }

    @GetMapping(value = "/payment/cancel")
    public RedirectView paymentCancel(HttpServletRequest request) {
       try {
           String paypalOrderId = request.getParameter("token");
//           OrderEntity order = orderRepository.findByPaypalOrderId(paypalOrderId)
//                   .orElseThrow(() -> new ResourceNotFoundException("Order", "paypal_order_id", paypalOrderId));

           RedirectView redirectView = new RedirectView();
           redirectView.setUrl("https://colux.vercel.app/colors/color-family/Red/240cdf5e-2ffe-4122-814e-a7221f26fda6");
           return redirectView;
       }catch (Exception ex){
           RedirectView redirectView = new RedirectView();
           redirectView.setUrl(FRONTEND_HOST + "/payment/cancel");
           return redirectView;
       }
    }

    private URI getUri() {
        return URI.create("/api/v1/orders");
    }
}
