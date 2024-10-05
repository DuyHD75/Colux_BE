package com.dcode.order_service.resource;

import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.cart.request.CartRequest;
import com.dcode.order_service.service.ICartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.order_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/carts")
@AllArgsConstructor
public class CartResource {

    private final ICartService cartService;

    @GetMapping("/test")
    public String test(HttpServletRequest request) {
      return "Cart service is up and running!";
    }

    @PostMapping
    public ResponseEntity<Response> createNewCart(
            @RequestBody @Valid CartRequest cartRequest, HttpServletRequest request
    ) {
        cartService.createClientCart(cartRequest);
        return ResponseEntity.created(getUri()).body(
                getResponse(request, "Cart created successfully!", CREATED, emptyMap())
        );
    }

    @GetMapping()
    public ResponseEntity<Response> getAllCarts(HttpServletRequest request) {

        var carts = cartService.getAllCarts();
        return ResponseEntity.ok().body(
                getResponse(request, "Carts retrieved successfully!", OK, Map.of("carts", carts))
        );
    }

    private URI getUri() {
        return URI.create("/api/v1/carts");
    }




}
