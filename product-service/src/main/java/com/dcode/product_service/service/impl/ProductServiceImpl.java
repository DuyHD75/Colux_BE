package com.dcode.product_service.service.impl;

import com.dcode.product_service.dto.CartDto;
import com.dcode.product_service.dtoRequest.ProductOrderRequest;
import com.dcode.product_service.dtoRequest.ProductRequest;
import com.dcode.product_service.dtoRequest.order_service.OrderLineDTO;
import com.dcode.product_service.dtoResponse.ProductOrderResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.enumeration.CategoryType;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.*;
import com.dcode.product_service.service.IProductService;
import com.dcode.product_service.utils.ProductUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public void createProducts(Set<ProductRequest> productRequest) {
        productRepository.saveAll(createNewProducts(productRequest));
    }

    private Product createNewProduct(ProductRequest productRequest) {
        var brand = getBrandByBrandId(productRequest.getBrandId());
        var category = getCategoryByCategoryId(productRequest.getCategoryId());
        var featureValues = getFeatureValueByFeatureValueIds(productRequest.getFeatureValueIds());
        var propertyValues = getPropertyValueByPropertyValueIds(productRequest.getPropertyValueIds());
        return createNewProductEntity(productRequest, brand, category, featureValues, propertyValues);
    }

    private Set<Product> createNewProducts(Set<ProductRequest> productRequests) {
        return productRequests.stream().map(
                this::createNewProduct
         ).collect(Collectors.toSet());
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

    public List<CartDto> checkStockAvailability(List<ProductOrderRequest> productOrderRequestList) {
        List<CartDto> cartDtos = new ArrayList<>();

        for (ProductOrderRequest productOrderRequest : productOrderRequestList) {
            CartDto cartDto = new CartDto();
            cartDto.setVariantId(productOrderRequest.getVariantId());

            if (productOrderRequest.getPaintId() != null) {
                Optional<PaintVariant> variantOpt = paintVariantRepository.findByPaint_paintIdAndVariant_variantId(
                        productOrderRequest.getPaintId(), productOrderRequest.getVariantId());
                if (variantOpt.isPresent()) {
                    PaintVariant variant = variantOpt.get();
                    cartDto.setVariantDescription(variant.getVariant().getSizeName());
                    cartDto.setCategoryName(variant.getVariant().getCategoryName());
                    cartDto.setPackageType(variant.getVariant().getPackageType());
                    cartDto.setVariantInventory(variant.getQuantity());
                    cartDto.setPriceSell(variant.getPrice());

                    CartDto.ClientProductResponse clientProductResponse = new CartDto.ClientProductResponse();
                    clientProductResponse.setProductId(variant.getPaint().getProduct().getProductId());
                    clientProductResponse.setProductName(variant.getPaint().getProduct().getProductName());
                    clientProductResponse.setProductImage(variant.getPaint().getProduct().getImages().isEmpty() ? null : variant.getPaint().getProduct().getImages().get(0).getUrl());
                    clientProductResponse.setCode(variant.getPaint().getProduct().getCode());

                    CartDto.PaintDetailsDto paintDetailsDto = new CartDto.PaintDetailsDto();
                    paintDetailsDto.setPaintId(variant.getPaint().getPaintId());
                    paintDetailsDto.setColorId(variant.getPaint().getColor().getColorId());
                    paintDetailsDto.setHex(variant.getPaint().getColor().getHex());
                    clientProductResponse.setPaintDetails(paintDetailsDto);

                    cartDto.setProductDetails(clientProductResponse);
                } else {
                    cartDto.setVariantDescription("Paint variant not found!");
                }
            } else if (productOrderRequest.getFloorId() != null) {
                Optional<FloorVariant> variantOpt = floorVariantRepository.findByFloor_floorIdAndVariant_VariantId(
                        productOrderRequest.getFloorId(), productOrderRequest.getVariantId());
                if (variantOpt.isPresent()) {
                    FloorVariant variant = variantOpt.get();
                    cartDto.setVariantDescription(variant.getVariant().getSizeName());
                    cartDto.setCategoryName(variant.getVariant().getCategoryName());
                    cartDto.setPackageType(variant.getVariant().getPackageType());
                    cartDto.setVariantInventory(variant.getQuantity());
                    cartDto.setPriceSell(variant.getPrice());

                    CartDto.ClientProductResponse clientProductResponse = new CartDto.ClientProductResponse();
                    clientProductResponse.setProductId(variant.getFloor().getProduct().getProductId());
                    clientProductResponse.setProductName(variant.getFloor().getProduct().getProductName());
                    clientProductResponse.setProductImage(variant.getFloor().getProduct().getImages().isEmpty() ? null : variant.getFloor().getProduct().getImages().get(0).getUrl());
                    clientProductResponse.setCode(variant.getFloor().getProduct().getCode());

                    CartDto.FloorDetailsDto floorDetailsDto = new CartDto.FloorDetailsDto();
                    floorDetailsDto.setFloorId(variant.getFloor().getFloorId());
                    cartDto.setProductDetails(clientProductResponse);
                    clientProductResponse.setFloorDetails(floorDetailsDto);
                } else {
                    cartDto.setVariantDescription("Floor variant not found!");
                }
            } else if (productOrderRequest.getWallpaperId() != null) {
                Optional<WallpaperVariant> variantOpt = wallpaperVariantRepository.findByWallpaper_wallpaperIdAndVariant_variantId(
                        productOrderRequest.getWallpaperId(), productOrderRequest.getVariantId());
                if (variantOpt.isPresent()) {
                    WallpaperVariant variant = variantOpt.get();
                    cartDto.setVariantDescription(variant.getVariant().getSizeName());
                    cartDto.setCategoryName(variant.getVariant().getCategoryName());
                    cartDto.setPackageType(variant.getVariant().getPackageType());
                    cartDto.setVariantInventory(variant.getQuantity());
                    cartDto.setPriceSell(variant.getPrice());

                    CartDto.ClientProductResponse clientProductResponse = new CartDto.ClientProductResponse();
                    clientProductResponse.setProductId(variant.getWallpaper().getProduct().getProductId());
                    clientProductResponse.setProductName(variant.getWallpaper().getProduct().getProductName());
                    clientProductResponse.setProductImage(variant.getWallpaper().getProduct().getImages().isEmpty() ? null : variant.getWallpaper().getProduct().getImages().get(0).getUrl());
                    clientProductResponse.setCode(variant.getWallpaper().getProduct().getCode());

                    CartDto.WallpaperDetailsDto wallpaperDetailsDto = new CartDto.WallpaperDetailsDto();
                    wallpaperDetailsDto.setWallpaperId(variant.getWallpaper().getWallpaperId());
                    clientProductResponse.setWallpaperDetails(wallpaperDetailsDto);

                    cartDto.setProductDetails(clientProductResponse);
                } else {
                    cartDto.setVariantDescription("Wallpaper variant not found!");
                }
            }

            cartDtos.add(cartDto);
        }

        return cartDtos;
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
        response.setProductId(variant.getPrice().toString());
        return (variant.getQuantity() >= request.getQuantity()) ? null :
                "Not enough stock for paint variant ID: " + request.getPaintId();
    }

    private String checkFloorStock(ProductOrderRequest request, ProductOrderResponse response) {
        FloorVariant variant = floorVariantRepository.findByFloor_floorIdAndVariant_VariantId(
                request.getFloorId(), request.getVariantId()).orElse(null);

        if (variant == null) {
            return "Floor variant not found!";
        }
        response.setPrice(variant.getPrice());
        response.setProductId(variant.getPrice().toString());
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
        response.setProductId(variant.getPrice().toString());
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
                FloorVariant variant = floorVariantRepository.findByFloor_floorIdAndVariant_VariantId(
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

    public PageResponse<ProductResponse> filterProducts(String type, List<String> features, List<String> properties, Double rating, Double minPrice, Double maxPrice, Pageable pageable) {
        Specification<Product> spec = new ProductSpecification(features, properties, rating, minPrice, maxPrice, type);
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        if (productPage.isEmpty()) {
            throw new ApiException("No products found with the given criteria");
        }

        Page<ProductResponse> productResponsePage = productPage.map(ProductUtils::fromProductEntity);
        return PageResponseBuilder.buildPageResponse(productResponsePage);
    }

    public String orderCancelRestore(List<OrderLineDTO> orderLineDTOList) {
        for (OrderLineDTO dto : orderLineDTOList) {
            if (dto.getPaintId() != null) {
                PaintVariant paintVariant = paintVariantRepository.findByPaint_paintIdAndVariant_variantId(dto.getPaintId(), dto.getVariantId())
                        .orElseThrow(() -> new ApiException("Paint not found!"));
                paintVariant.setQuantity(paintVariant.getQuantity() + dto.getQuantity());
                paintVariantRepository.save(paintVariant);
            } else if (dto.getWallpaperId() != null) {
                WallpaperVariant wallpaperVariant = wallpaperVariantRepository.findByWallpaper_wallpaperIdAndVariant_variantId(dto.getWallpaperId(), dto.getVariantId())
                        .orElseThrow(() -> new ApiException("Wallpaper not found!"));
                wallpaperVariant.setQuantity(wallpaperVariant.getQuantity() + dto.getQuantity());
                wallpaperVariantRepository.save(wallpaperVariant);
            } else if (dto.getFloorId() != null) {
                FloorVariant floorVariant = floorVariantRepository.findByFloor_floorIdAndVariant_VariantId(dto.getFloorId(), dto.getVariantId())
                        .orElseThrow(() -> new ApiException("Floor not found!"));
                floorVariant.setQuantity(floorVariant.getQuantity() + dto.getQuantity());
                floorVariantRepository.save(floorVariant);
            }
        }
        return "Order cancel restore successful";
    }


}

