package com.dcode.order_service.utils;

import com.dcode.order_service.dto.cart.UpdateQuantityType;
import com.dcode.order_service.dto.cart.request.CartRequest;
import com.dcode.order_service.dto.cart.request.CartVariantRequest;
import com.dcode.order_service.dto.cart.response.ClientCartResponse;
import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.entity.cart.CartEntity;
import com.dcode.order_service.entity.cart.CartVariantEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CartUtils {

    // TODO Create new cart entity
    public CartEntity createNewCartEntity(CartRequest request) {
        CartEntity cartEntity = CartEntity.builder()
                .cartId(UUID.randomUUID().toString())
                .customerId(request.getCustomerId())
                .status(request.getStatus())
                .build();

        Set<CartVariantEntity> cartVariants = request.getCartItems().stream()
                .map(cartItem -> requestToEntity(cartItem, cartEntity))
                .collect(Collectors.toSet());

        cartEntity.setCartVariants(cartVariants);

        return cartEntity;
    }


    private CartVariantEntity requestToEntity(CartVariantRequest request, CartEntity cartEntity) {
        var entity = new CartVariantEntity();
        entity.setVariantId(request.getVariantId());
        entity.setProductId(request.getProductId());
        entity.setPaintId(request.getPaintId());
        entity.setFloorId(request.getFloorId());
        entity.setWallpaperId(request.getWallpaperId());
        entity.setQuantity(request.getQuantity());
        entity.setCart(cartEntity);

        return entity;
    }

    private CartVariantEntity requestToEntity(CartVariantRequest request) {
        var entity = new CartVariantEntity();
        entity.setVariantId(request.getVariantId());
        entity.setProductId(request.getProductId());
        entity.setPaintId(request.getPaintId());
        entity.setWallpaperId(request.getFloorId());
        entity.setFloorId(request.getWallpaperId());
        entity.setQuantity(request.getQuantity());
        return entity;
    }
    // TODO Create new cart entity - END

    public CartEntity partialUpdate(CartEntity entity, CartRequest request) {
        List<String> currentVariantIds = entity.getCartVariants().stream()
                .map(CartVariantEntity::getVariantId)
                .collect(Collectors.toList());

        Set<CartVariantEntity> newCartVariants = new HashSet<>();

        for (CartVariantEntity cartVariant : entity.getCartVariants()) {
            for (CartVariantRequest clientCartVariantRequest : request.getCartItems()) {
                if (Objects.equals(cartVariant.getVariantId(), clientCartVariantRequest.getVariantId())) {
                    if (request.getUpdateQuantityType() == UpdateQuantityType.OVERRIDE) {
                        cartVariant.setQuantity(clientCartVariantRequest.getQuantity());
                    }
                    else if(request.getUpdateQuantityType() == UpdateQuantityType.DECREMENTAL){
                        if(cartVariant.getQuantity() - clientCartVariantRequest.getQuantity() < 0){
                            cartVariant.setQuantity(0);
                        }
                        else {
                            cartVariant.setQuantity(cartVariant.getQuantity() - clientCartVariantRequest.getQuantity());
                        }
                    }
                    else {
                        cartVariant.setQuantity(cartVariant.getQuantity() + clientCartVariantRequest.getQuantity());
                    }
                    break;
                }
            }
        }

        for (CartVariantRequest cartVariantRequest : request.getCartItems()) {
            if (!currentVariantIds.contains(cartVariantRequest.getVariantId())) {
                newCartVariants.add(requestToEntity(cartVariantRequest));
            }
        }

        entity.getCartVariants().addAll(newCartVariants);
        entity.setStatus(request.getStatus());
        attach(entity);
        return entity;
    }

    private static void attach(CartEntity cartEntity) {
        cartEntity.getCartVariants().forEach(cartVariant -> {
            cartVariant.setCart(cartEntity);
        });
    }


    // TODO Dang fix
    public ClientCartResponse entityToResponse(CartEntity cart, List<CartVariantResponse.ClientVariantResponse> variantResponses) {
        var cartResponse = new ClientCartResponse();
        cartResponse.setCartId(cart.getCartId());
        Set<CartVariantResponse> cartVariantResponses = new HashSet<>();
        for (CartVariantEntity cartVariantEntity : cart.getCartVariants()) {
            CartVariantResponse cartVariantResponse = new CartVariantResponse();

            cartVariantResponse.setCartItemQuantity(cartVariantEntity.getQuantity());

            for (CartVariantResponse.ClientVariantResponse variant : variantResponses) {
                if (variant.getVariantId().equals(cartVariantEntity.getVariantId())) {
                    cartVariantResponse.setCartItemVariant(variant);
                    break;
                }
            }

            cartVariantResponses.add(cartVariantResponse);
        }

        cartResponse.setCartItems(cartVariantResponses);

        return cartResponse;
    }
}
