package com.dcode.product_service.service.impl;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.ProductOrderRequest;
import com.dcode.product_service.dtoRequest.ProductRequest;
import com.dcode.product_service.dtoResponse.ProductOrderResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.enumeration.CategoryType;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.*;
import com.dcode.product_service.service.IProductService;
import com.dcode.product_service.utils.RequestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.ProductUtils.createNewProductEntity;
import static com.dcode.product_service.utils.ProductUtils.fromProductEntity;

@Service
@Transactional(rollbackFor = ApiException.class)
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService {

    private final PaintRepository paintRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final PaintVariantRepository paintVariantRepository;
    private final FloorVariantRepository floorVariantRepository;
    private final WallpaperVariantRepository wallpaperVariantRepository;
    private final FeatureValueRepository featureValueRepository;
    private final PropertyValueRepository propertyValueRepository;


    @Override
    public void createProduct(ProductRequest productRequest) {
        productRepository.save(createNewProduct(productRequest));
    }

    private Product createNewProduct(ProductRequest productRequest) {
        log.info(String.format("Creating new product: %s", productRequest.getProductName()));
        var brand = getBrandByBrandId(productRequest.getBrandId());
        var category = getCategoryByCategoryId(productRequest.getCategoryId());
        var featureValues = getFeatureValueByFeatureValueIds(productRequest.getFeatureValueIds());
        var propertyValues = getPropertyValueByPropertyValueIds(productRequest.getPropertyValueIds());
        return createNewProductEntity(productRequest, brand, category, featureValues, propertyValues);
    }

    private Set<PropertyValue> getPropertyValueByPropertyValueIds(Set<String> propertyValueIds) {
        Set<PropertyValue> propertyValues = propertyValueRepository.findByPropertyValueIdIn(propertyValueIds);
        Set<String> foundIds = propertyValues.stream()
                .map(PropertyValue::getPropertyValueId)
                .collect(Collectors.toSet());
        Set<String> missingIds = propertyValueIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());
        if (!missingIds.isEmpty()) {
            throw new ApiException("Property not found with propertyIds: " + missingIds);
        }
        return propertyValues;
    }

    private Set<FeatureValue> getFeatureValueByFeatureValueIds(Set<String> featureValueIds) {
        Set<FeatureValue> featureValues = featureValueRepository.findByFeatureValueIdIn(featureValueIds);
        Set<String> foundIds = featureValues.stream()
                .map(FeatureValue::getFeatureValueId)
                .collect(Collectors.toSet());
        Set<String> missingIds = featureValueIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());
        if (!missingIds.isEmpty()) {
            throw new ApiException("Feature not found with featureValueIds: " + missingIds);
        }
        return featureValues;
    }

    private Category getCategoryByCategoryId(String categoryId) {
        return categoryRepository.findCategoryByCategoryId(categoryId).orElseThrow(() -> new ApiException("Error: Category not found"));
    }

    private Brand getBrandByBrandId(String brandId) {
        return brandRepository.findBrandByBrandId(brandId).orElseThrow(() -> new ApiException("Error: Brand not found"));

    }


    @Override
    public List<ProductResponse> getAllProduct() {
        var products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<ProductResponse> getAllProduct(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        Page<ProductResponse> productResponses = products.map(this::mapToProductResponse);
        return PageResponseBuilder.buildPageResponse(productResponses);
    }


    public CategoryType getCategoryTypeFromName(String name) {
        for (CategoryType categoryType : CategoryType.values()) {
            if (categoryType.getName().equalsIgnoreCase(name)) {
                return categoryType;
            }
        }
        throw new IllegalArgumentException("Invalid category name: " + name);
    }
    public ProductResponse mapToProductResponse(Product product) {
        return fromProductEntity(product);
    }

    @Override
    public List<ProductOrderResponse> purchaseOrder(List<ProductOrderRequest> productOrderRequestList) {
        List<ProductOrderResponse> orderResponses = new ArrayList<>();
        List<String> insufficientStockMessages = new ArrayList<>();
        AtomicBoolean rollbackRequired = new AtomicBoolean(false);

        // Kiểm tra hàng tồn kho trước
        for (ProductOrderRequest productOrderRequest : productOrderRequestList) {
            ProductOrderResponse response = ProductOrderResponse.builder()
                    .paintId(productOrderRequest.getPaintId())
                    .wallpaperId(productOrderRequest.getWallpaperId())
                    .floorId(productOrderRequest.getFloorId())
                    .quantity(productOrderRequest.getQuantity())
                    .build();

            String stockCheckMessage = checkStock(productOrderRequest, response);
            if (stockCheckMessage != null) {
                response.setSuccess(false);
                response.setMessage(stockCheckMessage);
                insufficientStockMessages.add(stockCheckMessage); // Ghi lại thông báo không đủ hàng
                orderResponses.add(response);
                rollbackRequired.set(true); // Đánh dấu cần rollback
                continue; // Nếu không đủ hàng tồn kho, không thực hiện giảm số lượng
            }

            // Nếu tồn kho đủ, giảm số lượng và cập nhật phản hồi
            String message = reduceStock(productOrderRequest);
            response.setSuccess(true);
            response.setMessage(message != null ? message : "Stock updated successfully");

            orderResponses.add(response);
        }

        // Nếu cần rollback, ném ngoại lệ để rollback giao dịch
        if (rollbackRequired.get()) {
            log.error("One or more productOrderRequestList are out of stock, rolling back transaction.");
            throw new ApiException("One or more productOrderRequestList are out of stock, rolling back transaction.", orderResponses);
        }

        return orderResponses;
    }

//    // @AfterThrowing để xử lý ngoại lệ
//    @AfterThrowing(pointcut = "execution(* com.dcode.product_service.service.impl.ProductServiceImpl.purchaseOrder(..))", throwing = "exception")
//    public ResponseEntity<Response> handlePurchaseOrderException(Exception exception) {
//        log.error("Transaction rolled back due to: {}", exception.getMessage());
//
//        // Tạo phản hồi cho người dùng
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(RequestUtils.getErrorResponse(null, null, new ApiException("An unexpected error occurred."), HttpStatus.BAD_REQUEST));
//    }

    private String checkStock(ProductOrderRequest request, ProductOrderResponse response) {
        // Kiểm tra hàng tồn kho cho từng loại sản phẩm
        if (request.getPaintId() != null) {
            return checkPaintStock(request, response);
        } else if (request.getFloorId() != null) {
            return checkFloorStock(request, response);
        } else if (request.getWallpaperId() != null) {
            return checkWallpaperStock(request, response);
        }
        return null; // Nếu không có sản phẩm nào
    }

    private String checkPaintStock(ProductOrderRequest request, ProductOrderResponse response) {
        PaintVariant variant = paintVariantRepository.findByPaint_paintIdAndVariant_variantId(
                request.getPaintId(), request.getVariantId()).orElse(null);

        if (variant == null) {
            return "Paint variant not found!";
        }
        response.setPrice(variant.getPrice());
        return (variant.getQuantity() >= request.getQuantity()) ? null :
                "Not enough stock for paint variant ID: " + request.getPaintId();
    }

    private String checkFloorStock(ProductOrderRequest request, ProductOrderResponse response) {
        FloorVariant variant = floorVariantRepository.findByFloor_floorIDAndVariant_VariantId(
                request.getFloorId(), request.getVariantId()).orElse(null);

        if (variant == null) {
            return "Floor variant not found!";
        }
        response.setPrice(variant.getPrice());
        return (variant.getQuantity() >= request.getQuantity()) ? null :
                "Not enough stock for floor variant ID: " + request.getFloorId();
    }

    private String checkWallpaperStock(ProductOrderRequest request, ProductOrderResponse response) {
        WallpaperVariant variant = wallpaperVariantRepository.findByWallpaper_wallpaperIdAndVariant_variantId(
                request.getWallpaperId(), request.getVariantId()).orElse(null);

        if (variant == null) {
            return "Wallpaper variant not found!";
        }
        response.setPrice(variant.getPrice());
        return (variant.getQuantity() >= request.getQuantity()) ? null :
                "Not enough stock for wallpaper variant ID: " + request.getWallpaperId();
    }


    private String reduceStock(ProductOrderRequest request) {
        try {
            if (request.getPaintId() != null) {
                PaintVariant variant = paintVariantRepository.findByPaint_paintIdAndVariant_variantId(
                        request.getPaintId(), request.getVariantId()).orElseThrow(() ->
                        new ApiException("Paint variant not found for ID: " + request.getPaintId()));
                if (variant.getQuantity() < request.getQuantity()) {
                    return "Not enough stock.  " + variant.getQuantity() + "items remaining.";
                }
                variant.setQuantity((int) (variant.getQuantity() - request.getQuantity()));
                paintVariantRepository.save(variant);
                return "Stock updated successfully!";

            } else if (request.getFloorId() != null) {
                FloorVariant variant = floorVariantRepository.findByFloor_floorIDAndVariant_VariantId(
                        request.getFloorId(), request.getVariantId()).orElseThrow(() ->
                        new ApiException("Floor variant not found for ID: " + request.getFloorId()));
                if (variant.getQuantity() < request.getQuantity()) {
                    return "Not enough stock for floor variant ID: " + request.getFloorId();
                }
                variant.setQuantity(variant.getQuantity() - request.getQuantity());
                floorVariantRepository.save(variant);
                return "Stock updated successfully for floor variant ID: " + request.getFloorId();

            } else if (request.getWallpaperId() != null) {
                WallpaperVariant variant = wallpaperVariantRepository.findByWallpaper_wallpaperIdAndVariant_variantId(
                        request.getWallpaperId(), request.getVariantId()).orElseThrow(() ->
                        new ApiException("Wallpaper variant not found for ID: " + request.getWallpaperId()));
                if (variant.getQuantity() < request.getQuantity()) {
                    return "Not enough stock for wallpaper variant ID: " + request.getWallpaperId();
                }
                variant.setQuantity(variant.getQuantity() - request.getQuantity());
                wallpaperVariantRepository.save(variant);
                return "Stock updated successfully for wallpaper variant ID: " + request.getWallpaperId();
            }

            return "No valid product ID provided."; // Trả về thông báo khi không có ID sản phẩm hợp lệ
        } catch (Exception e) {
            // Ghi log lỗi cụ thể trong giảm số lượng
            log.error("Error reducing stock for request: {}", request, e);
            return "Error occurred while processing the request.";
        }
    }



}

