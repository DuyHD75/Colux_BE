package com.dcode.order_service.service.impl;

import com.dcode.order_service.dto.cart.request.CartRequest;
import com.dcode.order_service.dto.cart.request.CartVariantKeyRequest;
import com.dcode.order_service.dto.cart.request.CartVariantRequest;
import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.dto.cart.response.ClientCartResponse;
import com.dcode.order_service.entity.cart.CartEntity;
import com.dcode.order_service.entity.cart.CartVariantEntity;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.proxy.ICustomerClientProxy;
import com.dcode.order_service.proxy.ProductClientProxy;
import com.dcode.order_service.repository.ICartRepository;
import com.dcode.order_service.repository.ICartVariantRepository;
import com.dcode.order_service.service.ICartService;
import com.dcode.order_service.utils.CartUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements ICartService {

    private final ICustomerClientProxy clientProxy;
    private final ProductClientProxy productClientProxy;

    private final ICartRepository cartRepository;
    private final ICartVariantRepository cartVariantRepository;
    private final ApplicationEventPublisher publisher;
    private final CartUtils cartUtils;


    @Override
    public ClientCartResponse saveClientCart(CartRequest request) {

        final CartEntity cartBeforeSave;

        assert request.getCartId() != null;

        if (isCartIdInvalid(request.getCartId())) {
            cartBeforeSave = cartUtils.createNewCartEntity(request);
        } else {
            cartBeforeSave = cartRepository.findByCartId(request.getCartId())
                    .map(existingEntity -> cartUtils.partialUpdate(existingEntity, request))
                    .orElseThrow(() -> new BusinessException("Cannot create order :: No cart found with ID: " + request.getCartId()));
        }

        var customer = this.clientProxy.findUserByUserId(request.getCustomerId())
                .orElseThrow(() -> new BusinessException("Cannot create cart :: No customer found with ID: " + request.getCustomerId()));

        var variantResponses = this.productClientProxy.getProductByVariantId(request.getCartItems());

        for (CartVariantEntity cartVariant : cartBeforeSave.getCartVariants()) {
            for (var variant : variantResponses) {
                if (isVariantMatching(cartVariant, variant)) {
                    if (isQuantityExceedingInventory(cartVariant.getQuantity(), variant.getVariantInventory())) {
                        throw new BusinessException(String.format("Sorry, you can only purchase a maximum of %s products of this .",
                                variant.getVariantInventory(),
                                variant.getProductDetails().getProductName()), variant);
                    }
                }
            }
        }

        CartEntity cart = cartRepository.save(cartBeforeSave);
        log.info("Cart saved: {}", cart);

        return cartUtils.entityToResponse(cart, variantResponses);
    }

    private boolean isCartIdInvalid(String cartId) {
        return cartId.isEmpty() || cartId.isBlank();
    }

    private boolean isVariantMatching(CartVariantEntity cartVariant, CartVariantResponse.ClientVariantResponse variant) {
        boolean isPaintMatch = cartVariant.getPaintId() != null &&
                variant.getProductDetails().getPaintDetails() != null &&
                variant.getProductDetails().getPaintDetails().getPaintId().equals(cartVariant.getPaintId());

        boolean isWallpaperMatch = cartVariant.getWallpaperId() != null &&
                variant.getProductDetails().getWallpaperDetails() != null &&
                variant.getProductDetails().getWallpaperDetails().getWallpaperId().equals(cartVariant.getWallpaperId());

        boolean isFloorMatch = cartVariant.getFloorId() != null &&
                variant.getProductDetails().getFloorDetails() != null &&
                variant.getProductDetails().getFloorDetails().getFloorId().equals(cartVariant.getFloorId());

        return isPaintMatch || isWallpaperMatch || isFloorMatch;
    }


    private boolean isQuantityExceedingInventory(int quantity, int availableInventory) {
        return quantity > availableInventory;
    }


    @Override
    public ClientCartResponse getCart(String customerId) {
        if (customerId.isEmpty() || customerId.isBlank()) {
            throw new BusinessException("Cannot get cart :: Customer ID is empty");
        }

        var cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new BusinessException("Cannot get cart :: No cart found for customer ID: " + customerId));

        List<CartVariantRequest> cartItems = cart.getCartVariants().stream()
                .map(cartVariantEntity -> {
                    CartVariantRequest request = new CartVariantRequest();
                    request.setVariantId(cartVariantEntity.getVariantId());
                    request.setPaintId(cartVariantEntity.getPaintId());
                    request.setQuantity(cartVariantEntity.getQuantity());
                    request.setWallpaperId(cartVariantEntity.getWallpaperId());
                    request.setFloorId(cartVariantEntity.getFloorId());
                    return request;
                })
                .collect(Collectors.toList());

        var variantResponses = this.productClientProxy.getProductByVariantId(cartItems);

        return cartUtils.entityToResponse(cart, variantResponses);
    }

    @Override
    public void deleteCartItem(CartVariantKeyRequest idRequests) {
        if (idRequests.getCartId().isEmpty() || idRequests.getCartId().isBlank()) {
            throw new BusinessException("Cannot delete cart item :: Cart ID is empty");
        }

        if (idRequests.getItemDeleteRequests().isEmpty()) {
            throw new BusinessException("Cannot delete cart item :: Variant ID is empty");
        }

        var cart = cartRepository.findByCartId(idRequests.getCartId())
                .orElseThrow(() -> new BusinessException("Cannot delete cart item :: No cart found with ID: " + idRequests.getCartId()));

        for (Map.Entry<String, List<String>> entry : idRequests.getItemDeleteRequests().entrySet()) {
            String variantId = entry.getKey();
            List<String> productIDs = entry.getValue();
            List<String> paintIds = entry.getValue();
            List<String> floorIds = entry.getValue();
            List<String> wallpaperIds = entry.getValue();
            cartVariantRepository.deleteByCart_CartIdAndVariantIdAndProductIdInOrPaintIdInOrFloorIdInOrWallpaperIdIn(
                    cart.getCartId(), variantId, productIDs, paintIds, floorIds, wallpaperIds);
        }
    }

}
