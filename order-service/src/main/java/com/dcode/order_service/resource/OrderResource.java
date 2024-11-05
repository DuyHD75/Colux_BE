package com.dcode.order_service.resource;


import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.order.request.GhnCalculateFeeRequest;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.ConfirmedOrderResponse;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.exception.ResourceNotFoundException;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.service.IOrderService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import static java.util.Collections.emptyMap;

import java.net.URI;
import java.util.Map;

import static com.dcode.order_service.utils.RequestUtils.getResponse;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderResource {

    @Value("${application.host.frontend}")
    private String FRONTEND_HOST;

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

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getData());
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

    @PostMapping("/shipping/calculateFee")
    public ResponseEntity<Response> calculateFee(@RequestBody GhnCalculateFeeRequest ghnCalculateFeeRequestRequest, HttpServletRequest request) {
        var fee = orderService.calculateFee(ghnCalculateFeeRequestRequest);
        return ResponseEntity.ok().body(
                getResponse(request, "Fee calculated successfully!", OK, Map.of("fee", fee))
        );
    }
    @GetMapping("/shipping/province")
    public ResponseEntity<Response> getProvinces(HttpServletRequest request) {
        var provinces = orderService.getProvinces();
        return ResponseEntity.ok().body(
                getResponse(request, "Province list retrieved successfully!", HttpStatus.OK, Map.of("provinces", provinces))
        );
    }

    @PostMapping("/shipping/district")
    public ResponseEntity<Response> getDistrict(@RequestBody JsonNode districtId, HttpServletRequest request) {
        var fee = orderService.getDistrict(districtId);
        return ResponseEntity.ok().body(
                getResponse(request, "Districts retrieved successfully!", OK, Map.of("fee", fee))
        );
    }
    @PostMapping("/shipping/ward")
    public ResponseEntity<Response> getWard(@RequestBody JsonNode wardId, HttpServletRequest request) {
        var fee = orderService.getWard(wardId);
        return ResponseEntity.ok().body(
                getResponse(request, "Wards retrieved successfully!", OK, Map.of("fee", fee))
        );
    }
    
    private URI getUri() {
        return URI.create("/api/v1/orders");
    }
}
