package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.PreorderRequest;
import com.dcode.product_service.dtoRequest.ProductOrderRequest;
import com.dcode.product_service.dtoRequest.ProductRequest;
import com.dcode.product_service.dtoResponse.PreorderResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.enumeration.CategoryType;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.*;
import com.dcode.product_service.service.IProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.ProductUtils.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService {

    private final PaintRepository paintRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final PaintVariantRepository paintVariantRepository;
    private final ColorRepository colorRepository;
    private final VariantRepository variantRepository;
    private final FeatureValueRepository featureValueRepository;
    private final PropertyValueRepository propertyValueRepository;
//    private final


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


    public CategoryType getCategoryType(String categoryId) {
        if (categoryId.equals("paint_category_id")) {
            return CategoryType.PAINT;
        } else if (categoryId.equals("wallpaper_category_id")) {
            return CategoryType.WALLPAPER;
        } else if (categoryId.equals("floor_category_id")) {
            return CategoryType.FLOOR;
        } else {
            throw new IllegalArgumentException("Unknown categoryId: " + categoryId);
        }
    }

    public CategoryType getCategoryTypeFromName(String name) {
        for (CategoryType categoryType : CategoryType.values()) {
            if (categoryType.getName().equalsIgnoreCase(name)) {
                return categoryType;
            }
        }
        throw new IllegalArgumentException("Invalid category name: " + name);
    }

    public List<ProductOrderRequest> purchaseOrder(List<ProductOrderRequest> products) {
        boolean allInStock = true;
        for (ProductOrderRequest productOrderRequest : products) {
            Product product = productRepository.findByProductId(productOrderRequest.getProductId()).orElseThrow(() -> new ApiException("Product not found!"));
            CategoryType categoryType = getCategoryTypeFromName(product.getCategory().getName());

            boolean inStock = switch (categoryType) {
                case PAINT -> checkStockForPaint(productOrderRequest, product);
                case WALLPAPER -> checkStockForWallpaper(productOrderRequest);
                case FLOOR -> checkStockForFloor(productOrderRequest);
                default -> throw new IllegalArgumentException("Unknown category type: " + categoryType);
            };
            productOrderRequest.setSuccess(inStock);
            if (!inStock) allInStock = false;
        }
        if (allInStock) {
            for (ProductOrderRequest productOrderRequest : products) {
                Product product = productRepository.findByProductId(productOrderRequest.getProductId())
                        .orElseThrow(() -> new ApiException("Product not found!"));

                CategoryType categoryType = getCategoryTypeFromName(product.getCategory().getName());
                reduceStock(productOrderRequest, categoryType, product);
            }
        }
        return products;
    }


    public String getCategoryByProductId(String productId) {
        Product product = productRepository.findByProductId(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getCategory().getName();
    }

    public boolean checkStockForPaint(OrderRequest orderRequest, Product product) {
        Color color = colorRepository.findByColorId(orderRequest.getIdentity()).orElseThrow(() -> new ApiException("Color not found!"));
        Paint paint = paintRepository.findPaintByProductAndAndColor(product, color).orElseThrow(() -> new ApiException("Paint not found!"));
        Variant variant = variantRepository.findByVariantId(orderRequest.getVariantId()).orElseThrow(() -> new ApiException("variant not found!"));
        PaintVariant paintVariant = paintVariantRepository.findByPaintAndVariant(paint, variant)
                .orElseThrow(() -> new RuntimeException("Paint variant not found"));

        return paintVariant.getQuantity() >= orderRequest.getQuantity();
    }

    public boolean checkStockForWallpaper(ProductOrderRequest productOrderRequest) {
//        WallpaperVariant wallpaperVariant = wallpaperVariantRepository.findById(productOrder.getVariantId())
//                .orElseThrow(() -> new RuntimeException("Wallpaper variant not found"));
//
//        return wallpaperVariant.getQuantity() >= productOrder.getQuantity();
        return true;
    }

    public boolean checkStockForFloor(ProductOrderRequest productOrderRequest) {
//        FloorVariant floorVariant = floorVariantRepository.findById(productOrder.getVariantId())
//                .orElseThrow(() -> new RuntimeException("Floor variant not found"));
//
//        return floorVariant.getQuantity() >= productOrder.getQuantity();
        return true;
    }


    public ProductResponse mapToProductResponse(Product product) {
        return fromProductEntity(product);
    }


    public void reduceStock(OrderRequest orderRequest, CategoryType categoryType, Product product) {
        switch (categoryType) {
            case PAINT:
                Color color = colorRepository.findByColorId(orderRequest.getIdentity()).orElseThrow(() -> new ApiException("Color not found!"));
                Paint paint = paintRepository.findPaintByProductAndAndColor(product, color).orElseThrow(() -> new ApiException("Paint not found!"));
                Variant variant = variantRepository.findByVariantId(orderRequest.getVariantId()).orElseThrow(() -> new ApiException("variant not found!"));
                PaintVariant paintVariant = paintVariantRepository.findByPaintAndVariant(paint, variant)
                        .orElseThrow(() -> new RuntimeException("Paint variant not found"));

                paintVariant.setQuantity(paintVariant.getQuantity().intValue() - orderRequest.getQuantity().intValue());
                paintVariantRepository.save(paintVariant);
                break;

            case WALLPAPER:
//                // Truy vấn wallpaper_variant và giảm số lượng dựa trên productId và variantId
//                WallpaperVariant wallpaperVariant = wallpaperVariantRepository
//                        .findByProductIdAndVariantId(productOrder.getProductId(), productOrder.getVariantId())
//                        .orElseThrow(() -> new RuntimeException("Wallpaper variant not found"));
//
//                wallpaperVariant.setQuantity(wallpaperVariant.getQuantity() - productOrder.getQuantity());
//                wallpaperVariantRepository.save(wallpaperVariant);
                break;

            case FLOOR:
//                // Truy vấn floor_variant và giảm số lượng dựa trên productId và variantId
//                FloorVariant floorVariant = floorVariantRepository
//                        .findByProductIdAndVariantId(productOrder.getProductId(), productOrder.getVariantId())
//                        .orElseThrow(() -> new RuntimeException("Floor variant not found"));
//
//                floorVariant.setQuantity(floorVariant.getQuantity() - productOrder.getQuantity());
//                floorVariantRepository.save(floorVariant);
                break;
        }
    }


}
