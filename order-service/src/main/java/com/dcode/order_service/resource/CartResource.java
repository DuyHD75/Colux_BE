package com.dcode.order_service.resource;

import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.cart.request.CartRequest;
import com.dcode.order_service.dto.cart.request.CartVariantKeyRequest;
import com.dcode.order_service.dto.cart.response.ClientCartResponse;
import com.dcode.order_service.service.ICartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
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

    @PostMapping("/add-to-cart")
    public ResponseEntity<Response> createNewCart(
            @RequestBody @Valid CartRequest cartRequest, HttpServletRequest request
    ) {
        ClientCartResponse response =  cartService.createClientCart(cartRequest);
        return ResponseEntity.created(getUri()).body(
                getResponse(request, "Cart created successfully!", CREATED, Map.of("cart", response))
        );
    }

    @GetMapping("/{customer-id}")
    public ResponseEntity<Response> getCart(@PathVariable("customer-id") String customer_id, HttpServletRequest request) {
        var carts = cartService.getCart(customer_id);
        return ResponseEntity.ok().body(
                getResponse(request, "Carts retrieved successfully!", OK, Map.of("carts", carts))
        );
    }

    @DeleteMapping("/delete-cart-item")
    public ResponseEntity<Response> deleteCartItem(@RequestBody CartVariantKeyRequest idRequests, HttpServletRequest request) {
        cartService.deleteCartItem(idRequests);
        return ResponseEntity.ok().body(
                getResponse(request, "Cart item deleted successfully!", OK, emptyMap())
        );
    }



//    @PostMapping
//    public ResponseEntity<ClientCartResponse> saveCart(@RequestBody ClientCartRequest request) {
//        final Cart cartBeforeSave;
//
//        // TODO: Đôi khi cartId null nhưng thực tế user vẫn đang có cart trong DB
//        if (request.getCartId() == null) {
//            cartBeforeSave = clientCartMapper.requestToEntity(request);
//        } else {
//            cartBeforeSave = cartRepository.findById(request.getCartId())
//                    .map(existingEntity -> clientCartMapper.partialUpdate(existingEntity, request))
//                    .orElseThrow(() -> new ResourceNotFoundException(ResourceName.CART, FieldName.ID, request.getCartId()));
//        }
//
//        // Validate Variant Inventory
//        for (CartVariant cartVariant : cartBeforeSave.getCartVariants()) {
//            int inventory = InventoryUtils
//                    .calculateInventoryIndices(docketVariantRepository.findByVariantId(cartVariant.getCartVariantKey().getVariantId()))
//                    .get("canBeSold");
//            if (cartVariant.getQuantity() > inventory) {
//                throw new RuntimeException("Variant quantity cannot greater than variant inventory");
//            }
//        }
//
//        Cart cart = cartRepository.save(cartBeforeSave);
//        ClientCartResponse clientCartResponse = clientCartMapper.entityToResponse(cart);
//        return ResponseEntity.status(HttpStatus.OK).body(clientCartResponse);
//    }


    private URI getUri() {
        return URI.create("/api/v1/carts");
    }




}
