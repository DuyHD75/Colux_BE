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
import java.util.Set;
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
    public ClientCartResponse createClientCart(CartRequest request) {

        final CartEntity cartBeforeSave;

        if (request.getCartId().isEmpty() || request.getCartId().isBlank()) {
            cartBeforeSave = cartUtils.createNewCartEntity(request);
        } else {
            cartBeforeSave = cartRepository.findByCartId(request.getCartId())
                    .map(existingEntity -> cartUtils.partialUpdate(existingEntity, request))
                    .orElseThrow(() -> new BusinessException("Cannot create order :: No cart found with ID: " + request.getCartId()));
        }

        var customer = this.clientProxy.findUserByUserId(request.getCustomerId())
                .orElseThrow(() -> new BusinessException("Cannot create cart :: No customer found with ID: " + request.getCustomerId()));
        log.info("Customer found: {}", customer);

        // Step 3: Fetch product variant details from product service
        var variantResponses = this.productClientProxy.getProductByVariantId(request.getCartItems());

//        var variantResponses = this.mockProductVariantData(request.getCartItems());

        for (CartVariantEntity cartVariant : cartBeforeSave.getCartVariants()) {
            for (var variant : variantResponses) {
                if (cartVariant.getVariantId().equals(variant.getVariantId())) {
                    if (cartVariant.getQuantity() > variant.getVariantInventory()) {
                        throw new BusinessException(String.format("Only %s quantity remaining for item: %s",
                                variant.getVariantInventory(),
                                variant.getVariantId()));
                    }
                }
            }
        }

        CartEntity cart = cartRepository.save(cartBeforeSave);
        log.info("Cart saved: {}", cart);

        ClientCartResponse cartResponse = cartUtils.entityToResponse(cart, variantResponses);

        return cartResponse;
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
                    return request;
                })
                .collect(Collectors.toList());

//        var variantResponses = this.productClientProxy.getProductByVariantId(cartItems);
        var variantResponses = this.mockProductVariantData(cartItems);

        ClientCartResponse cartResponse = cartUtils.entityToResponse(cart, variantResponses);

        return cartResponse;
    }

    @Override
    public void deleteCartItem(CartVariantKeyRequest idRequests) {

        if (idRequests.getCartId().isEmpty() || idRequests.getCartId().isBlank()) {
            throw new BusinessException("Cannot delete cart item :: Cart ID is empty");
        }

        if (idRequests.getVariantIds().isEmpty()) {
            throw new BusinessException("Cannot delete cart item :: Variant ID is empty");
        }

        var cart = cartRepository.findByCartId(idRequests.getCartId())
                .orElseThrow(() -> new BusinessException("Cannot delete cart item :: No cart found with ID: " + idRequests.getCartId()));

        cartVariantRepository.deleteByCart_CartIdAndVariantIdIn(cart.getCartId(), idRequests.getVariantIds());
    }


    private List<CartVariantResponse.ClientVariantResponse> mockProductVariantData(List<CartVariantRequest> cartItems) {
        // Tạo dữ liệu mock cho variant responses
        return cartItems.stream().map(variantId -> {
            CartVariantResponse.ClientVariantResponse variantResponse = new CartVariantResponse.ClientVariantResponse();
            variantResponse.setVariantId("var-002");  // Mock variant ID cố định
            variantResponse.setVariantName("Blue Wallpaper");
            variantResponse.setVariantDescription("High quality blue wallpaper");
            variantResponse.setVariantInventory(10);  // Mock số lượng tồn kho

            // Mock product details
            CartVariantResponse.ClientVariantResponse.ClientProductResponse productResponse = new CartVariantResponse.ClientVariantResponse.ClientProductResponse();
            productResponse.setProductId("prod-003");
            productResponse.setProductName("Sơn Chống Thêm 2in1 -Dulux");
            productResponse.setProductImage("image-url");

            // Paint details không cần, để null
            productResponse.setPaintDetails(null);

            // Mock Wallpaper Details
            CartVariantResponse.ClientVariantResponse.ClientProductResponse.WallpaperDetails wallpaperDetails = new CartVariantResponse.ClientVariantResponse.ClientProductResponse.WallpaperDetails();
            wallpaperDetails.setWallpaperId("wall-001");
            wallpaperDetails.setWallpaperName("Ocean Blue");

            productResponse.setWallpaperDetails(wallpaperDetails);

            productResponse.setFloorDetails(null);
            variantResponse.setVariantProduct(productResponse);

            return variantResponse;
        }).collect(Collectors.toList());
    }
}
