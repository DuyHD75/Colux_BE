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
import java.util.stream.Stream;

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
        entity.setFloorId(request.getFloorId());
        entity.setWallpaperId(request.getWallpaperId());
        entity.setQuantity(request.getQuantity());
        return entity;
    }

    public CartEntity partialUpdate(CartEntity entity, CartRequest request) {
        List<String> currentVariantIds = entity.getCartVariants().stream()
                .map(CartVariantEntity::getVariantId)
                .collect(Collectors.toList());

        Set<CartVariantEntity> newCartVariants = new HashSet<>();

        for (CartVariantEntity cartVariant : entity.getCartVariants()) {
            for (CartVariantRequest clientCartVariantRequest : request.getCartItems()) {
                if (Objects.equals(cartVariant.getVariantId(), clientCartVariantRequest.getVariantId())) {
                    if (cartVariant.getPaintId() != null && cartVariant.getPaintId().equals(clientCartVariantRequest.getPaintId())) {
                        cartVariant.setQuantity(clientCartVariantRequest.getQuantity());
                    } else if (cartVariant.getPaintId() != null && !cartVariant.getPaintId().equals(clientCartVariantRequest.getPaintId())) {
                        newCartVariants.add(requestToEntity(clientCartVariantRequest));
                    } else if (cartVariant.getPaintId() == null) {
                        if (request.getUpdateQuantityType() == UpdateQuantityType.OVERRIDE) {
                            cartVariant.setQuantity(clientCartVariantRequest.getQuantity());
                        } else if (request.getUpdateQuantityType() == UpdateQuantityType.DECREMENTAL) {
                            if (cartVariant.getQuantity() - clientCartVariantRequest.getQuantity() < 0) {
                                cartVariant.setQuantity(0);
                            } else {
                                cartVariant.setQuantity(cartVariant.getQuantity() - clientCartVariantRequest.getQuantity());
                            }
                        } else {
                            cartVariant.setQuantity(cartVariant.getQuantity() + clientCartVariantRequest.getQuantity());
                        }
                        break;
                    }
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


    public ClientCartResponse entityToResponse(CartEntity cart, List<CartVariantResponse.ClientVariantResponse> variantResponses) {
        var cartResponse = new ClientCartResponse();
        cartResponse.setCartId(cart.getCartId());
        Set<CartVariantResponse> cartVariantResponses = new HashSet<>();
        for (CartVariantEntity cartVariantEntity : cart.getCartVariants()) {
            CartVariantResponse cartVariantResponse = getCartVariantResponse(variantResponses, cartVariantEntity);

            cartVariantResponses.add(cartVariantResponse);
        }

        cartResponse.setCartItems(cartVariantResponses);

        return cartResponse;
    }

    private static CartVariantResponse getCartVariantResponse(List<CartVariantResponse.ClientVariantResponse> variantResponses, CartVariantEntity cartVariantEntity) {
        CartVariantResponse cartVariantResponse = new CartVariantResponse();

        cartVariantResponse.setCartItemQuantity(cartVariantEntity.getQuantity());

        for (CartVariantResponse.ClientVariantResponse variant : variantResponses) {
            if (variant.getVariantId().equals(cartVariantEntity.getVariantId())) {
                if (cartVariantEntity.getPaintId() != null && cartVariantEntity.getPaintId().equals(variant.getProductDetails().getPaintDetails().getPaintId())) {
                    cartVariantResponse.setCartItemVariant(variant);
                } else if (cartVariantEntity.getFloorId() != null && cartVariantEntity.getFloorId().equals(variant.getProductDetails().getFloorDetails().getFloorId())) {
                    cartVariantResponse.setCartItemVariant(variant);
                } else if (cartVariantEntity.getWallpaperId() != null && cartVariantEntity.getWallpaperId().equals(variant.getProductDetails().getWallpaperDetails().getWallpaperId())) {
                    cartVariantResponse.setCartItemVariant(variant);
                }
            }
        }
        return cartVariantResponse;
    }


    public static List<CartVariantResponse.ClientVariantResponse> convertToClientVariantResponse(List<Map<String, Object>> variantResponses) {
        return variantResponses.stream().flatMap(product -> {
            CartVariantResponse.ClientVariantResponse clientVariantResponse = new CartVariantResponse.ClientVariantResponse();
            clientVariantResponse.setVariantId((String) product.get("variantId"));
            clientVariantResponse.setVariantDescription((String) product.get("variantDescription"));
            clientVariantResponse.setCategoryName((String) product.get("categoryName"));
            clientVariantResponse.setPackageType((String) product.get("packageType"));
            clientVariantResponse.setVariantInventory((Integer) product.get("variantInventory"));
            clientVariantResponse.setPriceSell((Double) product.get("priceSell"));

            Map<String, Object> productDetailsMap = (Map<String, Object>) product.get("productDetails");
            if (productDetailsMap != null) {
                CartVariantResponse.ClientVariantResponse.ClientProductResponse productDetails = new CartVariantResponse.ClientVariantResponse.ClientProductResponse();
                productDetails.setProductId((String) productDetailsMap.get("productId"));
                productDetails.setProductName((String) productDetailsMap.get("productName"));
                productDetails.setProductImage((String) productDetailsMap.get("productImage"));
                productDetails.setCode((String) productDetailsMap.get("code"));

                Map<String, Object> paintDetailsMap = (Map<String, Object>) productDetailsMap.get("paintDetails");
                if (paintDetailsMap != null) {
                    CartVariantResponse.ClientVariantResponse.ClientProductResponse.PaintDetails paintDetails = new CartVariantResponse.ClientVariantResponse.ClientProductResponse.PaintDetails();
                    paintDetails.setPaintId((String) paintDetailsMap.get("paintId"));
                    paintDetails.setColorId((String) paintDetailsMap.get("colorId"));
                    paintDetails.setHex((String) paintDetailsMap.get("hex"));
                    productDetails.setPaintDetails(paintDetails);
                }

                Map<String, Object> wallpaperDetailsMap = (Map<String, Object>) productDetailsMap.get("wallpaperDetails");
                if (wallpaperDetailsMap != null) {
                    CartVariantResponse.ClientVariantResponse.ClientProductResponse.WallpaperDetails wallpaperDetails = new CartVariantResponse.ClientVariantResponse.ClientProductResponse.WallpaperDetails();
                    wallpaperDetails.setWallpaperId((String) wallpaperDetailsMap.get("wallpaperId"));
                    productDetails.setWallpaperDetails(wallpaperDetails);
                }

                Map<String, Object> floorDetailsMap = (Map<String, Object>) productDetailsMap.get("floorDetails");
                if (floorDetailsMap != null) {
                    CartVariantResponse.ClientVariantResponse.ClientProductResponse.FloorDetails floorDetails = new CartVariantResponse.ClientVariantResponse.ClientProductResponse.FloorDetails();
                    floorDetails.setFloorId((String) floorDetailsMap.get("floorId"));
                    productDetails.setFloorDetails(floorDetails);
                }

                clientVariantResponse.setProductDetails(productDetails);
            }

            return Stream.of(clientVariantResponse);
        }).collect(Collectors.toList());
    }
}
