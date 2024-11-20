package com.dcode.product_service.service.impl;

import com.dcode.product_service.dto.BuildNameGHN;
import com.dcode.product_service.dto.CartDto;
import com.dcode.product_service.dto.CartDtoBase;
import com.dcode.product_service.dtoRequest.ProductOrderRequest;
import com.dcode.product_service.dtoRequest.ProductRequest;
import com.dcode.product_service.dtoRequest.ProductUpdateRequest;
import com.dcode.product_service.dtoRequest.order_service.OrderLineDTO;
import com.dcode.product_service.dtoResponse.*;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.enumeration.CategoryType;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.exception.BusinessException;
import com.dcode.product_service.repository.*;
import com.dcode.product_service.service.IProductService;
import com.dcode.product_service.utils.ProductUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.PaintUtils.convertVariantToVResponse;
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
    private final ImageRepository imageRepository;
    private final ColorRepository colorRepository;
    private final WallpaperRepository wallpaperRepository;
    private final FloorRepository floorRepository;
    private final VariantRepository variantRepository;
    private final SupplierRepository supplierRepository;
    private final ReviewRepository reviewRepository;
    private final EntityManager entityManager;


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
        var supplier = getProductSupplier(productRequest);
        Product product = createNewProductEntity(productRequest, brand, category, featureValues, propertyValues, supplier);
        Set<Image> images = productRequest.getImages().stream()
                .map(imageRequest -> Image.builder()
                        .imageId(UUID.randomUUID().toString())
                        .url(imageRequest)
                        .product(product)
                        .build())
                .collect(Collectors.toSet());
        product.setImages(images);
        imageRepository.saveAll(images);
        return product;
    }

    private ProductSupplier getProductSupplier(ProductRequest productRequest) {
        return supplierRepository.findBySupplierId(productRequest.getSupplierId())
                .orElseThrow(() -> new ApiException("Error: Supplier not found"));
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
        try {
            return products.stream()
                .map(this::mapToProductResponse)
                .toList();
        }catch (BusinessException e){
            throw new BusinessException("Error: Product not found");
        }

    }

    @Override
    public PageResponse<ProductResponse> getAllProduct(Pageable pageable) {
        Page<Product> products = productRepository.findProductsWithNonNullPaintWallpaperFloor(pageable);
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
    public List<ProductResponse> getProductDashboard(List<ProductOrderRequest> productDashboardRequests) {

        List<Product> products = productRepository.findAllByProductIdIn (
                productDashboardRequests.stream()
                        .map(ProductOrderRequest::getProductId)
                        .collect(Collectors.toList())
        );
//        if (products.size() != productDashboardRequests.size()) {
//
//        }
        return products.stream().map(ProductUtils::fromProductEntitySimple).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Long>> getDashboardInfo() {
        List<Map<String, Long>> dashboardInfo = new ArrayList<>();
        Long totalProduct = productRepository.count();
        Long totalBrand = brandRepository.count();
        Long totalSupplier = supplierRepository.count();
        Long totalReview = reviewRepository.count();
        dashboardInfo.add(Map.of("totalProduct", totalProduct));
        dashboardInfo.add(Map.of("totalBrand", totalBrand));
        dashboardInfo.add(Map.of("totalSupplier", totalSupplier));
        dashboardInfo.add(Map.of("totalReview", totalReview));
        return dashboardInfo;
    }

    @Override
    public ProductResponse getProductByProductId(String productId) {
        return productRepository.findByProductId(productId)
                .map(this::mapToProductResponse)
                .orElseThrow(() -> new ApiException("Product not found: " + productId));
    }


    @Override
    public List<CartDtoBase> checkStockAvailability(List<ProductOrderRequest> productOrderRequestList, boolean isBuildNameGHN) {
        List<CartDtoBase> cartDtoBases = new ArrayList<>();

        for (ProductOrderRequest productOrderRequest : productOrderRequestList) {
            CartDtoBase cartDto;
            if (isBuildNameGHN) {
                BuildNameGHN buildNameGHN = new BuildNameGHN();
                Optional<VariantResponse> variantResponseOpt = convertVariantToVResponse(getVariants(productOrderRequest)).stream().findFirst();
                variantResponseOpt.ifPresent(buildNameGHN::setVariantResponse);
                cartDto = buildNameGHN;
            } else {
                CartDto oldCartDto = new CartDto();
                oldCartDto.setVariantId(productOrderRequest.getVariantId());
                cartDto = oldCartDto;
            }

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

                    CartDtoBase.ClientProductResponse clientProductResponse = new CartDtoBase.ClientProductResponse();
                    clientProductResponse.setProductId(variant.getPaint().getProduct().getProductId());
                    clientProductResponse.setProductName(variant.getPaint().getProduct().getProductName());
                    clientProductResponse.setProductImage(variant.getPaint().getProduct().getImages().isEmpty() ? null : variant.getPaint().getProduct().getImages().iterator().next().getUrl());
                    clientProductResponse.setProductDescription(variant.getPaint().getProduct().getDescription());
                    clientProductResponse.setCode(variant.getPaint().getProduct().getCode());

                    CartDtoBase.PaintDetailsDto paintDetailsDto = new CartDtoBase.PaintDetailsDto();
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

                    CartDtoBase.ClientProductResponse clientProductResponse = new CartDtoBase.ClientProductResponse();
                    clientProductResponse.setProductId(variant.getFloor().getProduct().getProductId());
                    clientProductResponse.setProductName(variant.getFloor().getProduct().getProductName());
                    clientProductResponse.setProductImage(variant.getFloor().getProduct().getImages().isEmpty() ? null : variant.getFloor().getProduct().getImages().iterator().next().getUrl());
                    clientProductResponse.setProductDescription(variant.getFloor().getProduct().getDescription());
                    clientProductResponse.setCode(variant.getFloor().getProduct().getCode());

                    CartDtoBase.FloorDetailsDto floorDetailsDto = new CartDtoBase.FloorDetailsDto();
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

                    CartDtoBase.ClientProductResponse clientProductResponse = new CartDtoBase.ClientProductResponse();
                    clientProductResponse.setProductId(variant.getWallpaper().getProduct().getProductId());
                    clientProductResponse.setProductName(variant.getWallpaper().getProduct().getProductName());
                    clientProductResponse.setProductImage(variant.getWallpaper().getProduct().getImages().isEmpty() ? null : variant.getWallpaper().getProduct().getImages().iterator().next().getUrl());
                    clientProductResponse.setProductDescription(variant.getWallpaper().getProduct().getDescription());
                    clientProductResponse.setCode(variant.getWallpaper().getProduct().getCode());

                    CartDtoBase.WallpaperDetailsDto wallpaperDetailsDto = new CartDtoBase.WallpaperDetailsDto();
                    wallpaperDetailsDto.setWallpaperId(variant.getWallpaper().getWallpaperId());
                    clientProductResponse.setWallpaperDetails(wallpaperDetailsDto);

                    cartDto.setProductDetails(clientProductResponse);
                } else {
                    cartDto.setVariantDescription("Wallpaper variant not found!");
                }
            }

            cartDtoBases.add(cartDto);
        }

        return cartDtoBases;
    }

    private Set<Object> getVariants(ProductOrderRequest productOrderRequest) {
        Set<Object> variants = new HashSet<>();
        if (productOrderRequest.getPaintId() != null) {
            Optional<PaintVariant> variantOpt = paintVariantRepository.findByPaint_paintIdAndVariant_variantId(
                    productOrderRequest.getPaintId(), productOrderRequest.getVariantId());
            variantOpt.ifPresent(variants::add);
        } else if (productOrderRequest.getFloorId() != null) {
            Optional<FloorVariant> variantOpt = floorVariantRepository.findByFloor_floorIdAndVariant_VariantId(
                    productOrderRequest.getFloorId(), productOrderRequest.getVariantId());
            variantOpt.ifPresent(variants::add);
        } else if (productOrderRequest.getWallpaperId() != null) {
            Optional<WallpaperVariant> variantOpt = wallpaperVariantRepository.findByWallpaper_wallpaperIdAndVariant_variantId(
                    productOrderRequest.getWallpaperId(), productOrderRequest.getVariantId());
            variantOpt.ifPresent(variants::add);
        }
        return variants;
    }

    @Override
    public void updateProduct(ProductUpdateRequest productRequest) {
        Product product = productRepository.findByProductId(productRequest.getProductId())
                .orElseThrow(() -> new BusinessException("Product not found!"));

        // Cập nhật thông tin sản phẩm
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setPlaceOfOrigin(productRequest.getPlaceOfOrigin());
        product.setRatingAverage(productRequest.getRatingAverage());
        product.setCode(productRequest.getCode());
        product.setWarranty(productRequest.getWarranty());
        product.setApplicableSurface(productRequest.getApplicableSurface());
        product.setCategory(categoryRepository.findCategoryByCategoryId(productRequest.getCategory().getCategoryId())
                .orElseThrow(() -> new BusinessException("Category not found!")));
        product.setBrand(brandRepository.findBrandByBrandId(productRequest.getBrand().getBrandId())
                .orElseThrow(() -> new BusinessException("Brand not found!")));

        // Xử lý hình ảnh
        Set<String> updatedImageIds = productRequest.getImages().stream()
                .map(ImageResponse::getImageId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        product.getImages().removeIf(image -> {
            boolean shouldRemove = !updatedImageIds.contains(image.getImageId());
            if (shouldRemove) {
                imageRepository.delete(image); // Xóa khỏi bảng image
            }
            return shouldRemove;
        });

        for (ImageResponse image : productRequest.getImages()) {
            if (image.getImageId() != null) {
                Image existingImage = imageRepository.findByImageId(image.getImageId())
                        .orElseThrow(() -> new BusinessException("Image not found: " + image.getImageId()));
                existingImage.setUrl(image.getUrl());
                imageRepository.save(existingImage);
            } else {
                Image newImage = Image.builder()
                        .imageId(UUID.randomUUID().toString())
                        .product(product)
                        .url(image.getUrl())
                        .build();
                imageRepository.save(newImage);
                product.getImages().add(newImage);
            }
        }

        // Cập nhật các featureValues từ request
        Set<String> updatedFeatureIds = productRequest.getFeatures().stream()
                .map(FeatureValueResponse::getFeatureValueId)
                .collect(Collectors.toSet());

        product.getFeatureValues().removeIf(featureValue -> {
            if (!updatedFeatureIds.contains(featureValue.getFeatureValueId())) {
                featureValueRepository.delete(featureValue); // Xóa featureValue khỏi database
                return true;
            }
            return false;
        });

        // Thêm các feature mới từ request
        for (FeatureValueResponse featureValue : productRequest.getFeatures()) {
            if (featureValue.getFeatureValueId() != null) {
                FeatureValue existingFeatureValue = featureValueRepository.findByFeatureValueId(featureValue.getFeatureValueId())
                        .orElseThrow(() -> new BusinessException("Feature value not found: " + featureValue.getFeatureValueId()));
                product.getFeatureValues().add(existingFeatureValue);
            }
        }

        // Cập nhật các propertyValues từ request
        Set<String> updatedPropertyIds = productRequest.getProperties().stream()
                .map(PropertyValueResponse::getPropertyValueId)
                .collect(Collectors.toSet());

        product.getPropertyValues().removeIf(propertyValue -> {
            if (!updatedPropertyIds.contains(propertyValue.getPropertyValueId())) {
                propertyValueRepository.delete(propertyValue); // Xóa propertyValue khỏi database
                return true;
            }
            return false;
        });

        // Thêm các property mới từ request
        for (PropertyValueResponse propertyValue : productRequest.getProperties()) {
            if (propertyValue.getPropertyValueId() != null) {
                PropertyValue existingPropertyValue = propertyValueRepository.findByPropertyValueId(propertyValue.getPropertyValueId())
                        .orElseThrow(() -> new BusinessException("Property value not found: " + propertyValue.getPropertyValueId()));
                product.getPropertyValues().add(existingPropertyValue);
            }
        }



        updatePaints(productRequest.getPaints(), product);
        updateWallpapers(productRequest.getWallpapers(), product);
        updateFloors(productRequest.getFloors(), product);



        productRepository.save(product);
    }



    private void updatePaints(Set<PaintResponse> paintRequests, Product product) {
        Set<Paint> existingPaints = new HashSet<>(product.getPaints());

        // Xóa các Paint và PaintVariant không còn được tham chiếu
        if (paintRequests == null) {
            for (Paint paint : existingPaints) {
                paintVariantRepository.deleteAll(paint.getPaintVariants());
                product.getPaints().remove(paint); // Xóa khỏi product
                paintRepository.delete(paint);
            }
            return;
        }

        // Cập nhật Paint
        Set<String> requestedPaintIds = paintRequests.stream()
                .map(PaintResponse::getPaintId)
                .collect(Collectors.toSet());

        for (Paint paint : existingPaints) {
            if (!requestedPaintIds.contains(paint.getPaintId())) {
                paintVariantRepository.deleteAll(paint.getPaintVariants()); // Xóa các PaintVariant trước
                product.getPaints().remove(paint); // Xóa khỏi product
                paintRepository.delete(paint);
            }
        }

        // Cập nhật hoặc thêm mới Paint
        for (PaintResponse paintRequest : paintRequests) {
            if (paintRequest.getPaintId() == null) {
                // Thêm mới Paint
                Paint newPaint = Paint.builder()
                        .product(product)
                        .paintId(UUID.randomUUID().toString())
                        .status(paintRequest.getStatus())
                        .color(colorRepository.findByColorId(paintRequest.getColor().getColorId())
                                .orElseThrow(() -> new BusinessException("Color not found: " + paintRequest.getColor().getColorId())))
                        .paintVariants(new HashSet<>())
                        .build();
                paintRepository.save(newPaint);
                product.getPaints().add(newPaint);
                updatePaintVariants(paintRequest.getVariants(), newPaint);
            } else {
                // Cập nhật Paint hiện có
                Paint existingPaint = paintRepository.findByPaintId(paintRequest.getPaintId())
                        .orElseThrow(() -> new BusinessException("Paint not found: " + paintRequest.getPaintId()));
                existingPaint.setStatus(paintRequest.getStatus());
                existingPaint.setColor(colorRepository.findByColorId(paintRequest.getColor().getColorId())
                        .orElseThrow(() -> new BusinessException("Color not found: " + paintRequest.getColor().getColorId())));
                paintRepository.save(existingPaint);
                updatePaintVariants(paintRequest.getVariants(), existingPaint);
            }
        }
    }



    private void updatePaintVariants(List<VariantResponse> paintVariantRequests, Paint paint) {
        // Tạo tập hợp chứa các ID của PaintVariant có trong request
        Set<String> requestVariantIds = paintVariantRequests.stream()
                .map(VariantResponse::getVariantId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (VariantResponse variantRequest : paintVariantRequests) {
            // Chỉ cập nhật những PaintVariant đã tồn tại trong DB hoặc tạo mới nếu cần
            PaintVariant existingVariant = paintVariantRepository.findByPaint_paintIdAndVariant_variantId(
                            paint.getPaintId(), variantRequest.getVariantId())
                    .orElseGet(() -> PaintVariant.builder()
                            .paintVariantId(UUID.randomUUID().toString())
                            .paint(paint)
                            .variant(variantRepository.findByVariantId(variantRequest.getVariantId())
                                    .orElseThrow(() -> new BusinessException("Variant not found: " + variantRequest.getVariantId())))
                            .build());

            // Cập nhật các thuộc tính của PaintVariant
            existingVariant.setQuantity(variantRequest.getQuantity());
            existingVariant.setPrice(variantRequest.getPrice());

            // Lưu PaintVariant đã cập nhật
            paintVariantRepository.save(existingVariant);
        }

        // Xác định và xóa các PaintVariant không có trong request
        List<PaintVariant> variantsToRemove = paint.getPaintVariants().stream()
                .filter(variant -> !requestVariantIds.contains(variant.getVariant().getVariantId()))
                .toList();

        for (PaintVariant variant : variantsToRemove) {
            paint.getPaintVariants().remove(variant);
            paintVariantRepository.delete(variant);
        }
    }


    private void updateWallpapers(Set<WallpaperResponse> wallpaperRequests, Product product) {
        Set<Wallpaper> existingWallpapers = new HashSet<>(product.getWallpapers());

        if (wallpaperRequests == null) {
            for (Wallpaper wallpaper : existingWallpapers) {
                Set<WallpaperVariant> variants = wallpaper.getWallpaperVariants();
                wallpaperVariantRepository.deleteAll(variants);
                product.getWallpapers().remove(wallpaper); // Xóa khỏi product
                wallpaperRepository.delete(wallpaper);
            }
            return;
        }

        // Tạo tập hợp chứa các WallpaperId từ yêu cầu cập nhật
        Set<String> requestedWallpaperIds = wallpaperRequests.stream()
                .map(WallpaperResponse::getWallpaperId)
                .collect(Collectors.toSet());

        // Xóa các Wallpaper không có trong yêu cầu
        for (Wallpaper wallpaper : existingWallpapers) {
            if (!requestedWallpaperIds.contains(wallpaper.getWallpaperId())) {
                // Xóa các WallpaperVariant liên kết với Wallpaper trước khi xóa Wallpaper
                Set<WallpaperVariant> variants = wallpaper.getWallpaperVariants();
                wallpaperVariantRepository.deleteAll(variants);
                product.getWallpapers().remove(wallpaper); // Xóa khỏi product
                wallpaperRepository.delete(wallpaper);
            }
        }

        for (WallpaperResponse wallpaperRequest : wallpaperRequests) {
            if (wallpaperRequest.getWallpaperId() == null) {
                // Thêm mới wallpaper
                Wallpaper newWallpaper = Wallpaper.builder()
                        .wallpaperId(UUID.randomUUID().toString())
                        .product(product)
                        .status(wallpaperRequest.getStatus())
                        .wallpaperVariants(new HashSet<>())
                        .build();
                product.getWallpapers().add(newWallpaper);
                wallpaperRepository.save(newWallpaper);
                updateWallpaperVariants(wallpaperRequest.getVariants(), newWallpaper);
            } else {
                // Cập nhật wallpaper hiện có
                Wallpaper existingWallpaper = wallpaperRepository.findByWallpaperId(wallpaperRequest.getWallpaperId())
                        .orElseThrow(() -> new ApiException("Wallpaper not found: " + wallpaperRequest.getWallpaperId()));
                existingWallpaper.setStatus(wallpaperRequest.getStatus());
                wallpaperRepository.save(existingWallpaper);
                updateWallpaperVariants(wallpaperRequest.getVariants(), existingWallpaper);
            }
        }
    }

    private void updateWallpaperVariants(List<VariantResponse> wallpaperVariantRequests, Wallpaper wallpaper) {
        // Lấy danh sách các variantId có trong yêu cầu
        Set<String> requestVariantIds = wallpaperVariantRequests.stream()
                .map(VariantResponse::getVariantId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Thêm hoặc cập nhật WallpaperVariant
        for (VariantResponse variantRequest : wallpaperVariantRequests) {
            if (variantRequest.getVariantId() != null) {
                WallpaperVariant existingVariant = wallpaperVariantRepository
                        .findByWallpaper_wallpaperIdAndVariant_variantId(wallpaper.getWallpaperId(), variantRequest.getVariantId())
                        .orElse(WallpaperVariant.builder()
                                .wallpaperVariantId(UUID.randomUUID().toString())
                                .wallpaper(wallpaper)
                                .variant(variantRepository.findByVariantId(variantRequest.getVariantId())
                                        .orElseThrow(() -> new BusinessException("Variant not found: " + variantRequest.getVariantId())))
                                .build());

                existingVariant.setQuantity(variantRequest.getQuantity());
                existingVariant.setPrice(variantRequest.getPrice());
                wallpaperVariantRepository.save(existingVariant);
            }
        }

        // Xóa WallpaperVariant không có trong yêu cầu
        wallpaper.getWallpaperVariants().removeIf(variant -> {
            boolean shouldRemove = !requestVariantIds.contains(variant.getVariant().getVariantId());
            if (shouldRemove) {
                wallpaperVariantRepository.delete(variant);
            }
            return shouldRemove;
        });
    }


    private void updateFloors(Set<FloorResponse> floorRequests, Product product) {
        Set<Floor> existingFloors = new HashSet<>(product.getFloors());

        if (floorRequests == null) {
            for (Floor floor : existingFloors) {
                Set<FloorVariant> variants = floor.getFloorVariants();
                floorVariantRepository.deleteAll(variants);
                product.getFloors().remove(floor); // Xóa khỏi product
                floorRepository.delete(floor);
            }
            return;
        }

        Set<String> requestedFloorIds = floorRequests.stream()
                .map(FloorResponse::getFloorId)
                .collect(Collectors.toSet());

        for (Floor floor : existingFloors) {
            if (!requestedFloorIds.contains(floor.getFloorId())) {
                Set<FloorVariant> variants = floor.getFloorVariants();
                floorVariantRepository.deleteAll(variants);
                product.getFloors().remove(floor); // Xóa khỏi product
                floorRepository.delete(floor);
            }
        }

        for (FloorResponse floorRequest : floorRequests) {
            if (floorRequest.getFloorId() == null) {
                Floor newFloor = Floor.builder()
                        .floorId(UUID.randomUUID().toString())
                        .product(product)
                        .foamThickness(floorRequest.getFoamThickness())
                        .numberOfPiecesPerBox(floorRequest.getNumberOfPiecesPerBox())
                        .status(floorRequest.getStatus())
                        .floorVariants(new HashSet<>())
                        .build();
                product.getFloors().add(newFloor);
                floorRepository.save(newFloor);
                updateFloorVariants(floorRequest.getVariants(), newFloor);
            } else {
                Floor existingFloor = floorRepository.findByFloorId(floorRequest.getFloorId())
                        .orElseThrow(() -> new ApiException("Floor not found: " + floorRequest.getFloorId()));
                existingFloor.setStatus(floorRequest.getStatus());
                floorRepository.save(existingFloor);
                updateFloorVariants(floorRequest.getVariants(), existingFloor);
            }
        }
    }

    private void updateFloorVariants(List<VariantResponse> floorVariantRequests, Floor floor) {
        // Lấy danh sách các variantId có trong yêu cầu
        Set<String> requestVariantIds = floorVariantRequests.stream()
                .map(VariantResponse::getVariantId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Thêm hoặc cập nhật FloorVariant
        for (VariantResponse variantRequest : floorVariantRequests) {
            if (variantRequest.getVariantId() != null) {
                FloorVariant existingVariant = floorVariantRepository
                        .findByFloor_floorIdAndVariant_VariantId(floor.getFloorId(), variantRequest.getVariantId())
                        .orElse(FloorVariant.builder()
                                .floorVariantId(UUID.randomUUID().toString())
                                .floor(floor)
                                .variant(variantRepository.findByVariantId(variantRequest.getVariantId())
                                        .orElseThrow(() -> new BusinessException("Variant not found: " + variantRequest.getVariantId())))
                                .build());

                existingVariant.setQuantity(variantRequest.getQuantity());
                existingVariant.setPrice(variantRequest.getPrice());
                floorVariantRepository.save(existingVariant);
            }
        }

        // Xóa FloorVariant không có trong yêu cầu
        floor.getFloorVariants().removeIf(variant -> {
            boolean shouldRemove = !requestVariantIds.contains(variant.getVariant().getVariantId());
            if (shouldRemove) {
                floorVariantRepository.delete(variant);
            }
            return shouldRemove;
        });
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
                    .variantId(productOrderRequest.getVariantId())
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
        response.setProductId(variant.getPaint().getProduct().getProductId());
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
        response.setProductId(variant.getFloor().getProduct().getProductId());
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
        response.setProductId(variant.getWallpaper().getProduct().getProductId());
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

    public PageResponse<ProductResponse> filterProducts(String type, List<String> features, List<String> properties, Double minPrice, Double maxPrice, Pageable pageable) {
    long propertyCount = properties != null ? properties.size() : 0;
    long featureCount = features != null ? features.size() : 0;
    Double minValue = Double.MIN_VALUE;
    Double maxValue = Double.MAX_VALUE;
    Page<Product> productPage = productRepository.filterProductsNative(type, minPrice, maxPrice, properties, features, propertyCount, featureCount, pageable);

    if (productPage.isEmpty()) {
        return PageResponseBuilder.buildPageResponse(Page.empty());
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

