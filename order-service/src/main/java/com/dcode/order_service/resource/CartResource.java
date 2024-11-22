package com.dcode.order_service.resource;

import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.cart.request.CartRequest;
import com.dcode.order_service.dto.cart.request.CartVariantKeyRequest;
import com.dcode.order_service.dto.cart.response.ClientCartResponse;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.service.ICartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.order_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/carts")
@AllArgsConstructor
public class CartResource {

    private final ICartService cartService;

    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        return "Cart service is up and running!";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add-to-cart")
    public ResponseEntity<Response> createNewCart(
            @RequestBody @Valid CartRequest cartRequest, HttpServletRequest request
    ) {
        try {
            ClientCartResponse response = cartService.saveClientCart(cartRequest);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, "Cart created successfully!", CREATED, Map.of("cart", response))
            );
        } catch (BusinessException ex) {
            return ResponseEntity.internalServerError().body(getResponse(request, "Error: " + ex.getMessage(), INTERNAL_SERVER_ERROR, Map.of("errorData", ex.getData())));
        }
        catch (Exception ex) {
            return ResponseEntity.internalServerError().body(getResponse(request, "Error: " + ex.getMessage(), INTERNAL_SERVER_ERROR, emptyMap()));
        }
    }

    @GetMapping("/{customer-id}")
    public ResponseEntity<Response> getCart(@PathVariable("customer-id") String customer_id, HttpServletRequest request) {
        try {
            var carts = cartService.getCart(customer_id);
            return ResponseEntity.ok().body(
                    getResponse(request, "Carts retrieved successfully!", OK, Map.of("carts", carts))
            );
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(getResponse(request, "Error: " + ex.getMessage(), INTERNAL_SERVER_ERROR, emptyMap()));
        }
    }

    @DeleteMapping("/delete-cart-item")
    public ResponseEntity<Response> deleteCartItem(@RequestBody CartVariantKeyRequest idRequests, HttpServletRequest request) {
        try {
            cartService.deleteCartItem(idRequests);
            return ResponseEntity.ok().body(
                    getResponse(request, "Cart item deleted successfully!", OK, emptyMap())
            );
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(getResponse(request, "Error: " + ex.getMessage(), INTERNAL_SERVER_ERROR, emptyMap()));
        }
    }

    private URI getUri() {
        return URI.create("/api/v1/carts");
    }


}