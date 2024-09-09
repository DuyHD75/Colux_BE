package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.ProductOrder;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.enumeration.CategoryType;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.*;
import com.dcode.product_service.service.IProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.ProductUtils.createNewProductEntity;
import static com.dcode.product_service.utils.ProductUtils.fromProductEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService {

    private final PaintRepository paintRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final WallpaperRepository wallpaperRepository;
    private final PaintVariantRepository paintVariantRepository;
    private final WallpaperVariantRepository wallpaperVariantRepository;
    private final FloorVariantRepository floorVariantRepository;
    private final ColorRepository colorRepository;
    private final VariantRepository variantRepository;
//    private final



    @Override
    public void createProduct(String description,
                              String placeOfOrigin,
                              String price,
                              String productName,
                              String ratingAverage,
                              String warranty,
                              String brandId,
                              String categoryId) {
        productRepository.save(createNewProduct(description,
                placeOfOrigin,
                price,
                productName,
                ratingAverage,
                warranty,
                brandId,
                categoryId));
    }

    private Product createNewProduct(String description, String placeOfOrigin, String price, String productName, String ratingAverage, String warranty, String brandId, String categoryId) {
        log.info(String.format("Creating new product: %s", productName));
        var brand = getBrandByBrandId(brandId);
        var category = getCategoryByCategoryId(categoryId);
        return createNewProductEntity(description, placeOfOrigin, price, productName, ratingAverage, warranty, brand, category);
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

    private CategoryType getCategoryTypeFromName(String name) {
        for (CategoryType categoryType : CategoryType.values()) {
            if (categoryType.getName().equalsIgnoreCase(name)) {
                return categoryType;
            }
        }
        throw new IllegalArgumentException("Invalid category name: " + name);
    }

    public List<ProductOrder> purchaseOrder(List<ProductOrder> products) {
        for (ProductOrder productOrder : products) {
            Product product = productRepository.findByProductId(productOrder.getProductId()).orElseThrow(() -> new ApiException("Product not found!"));
            CategoryType categoryType = getCategoryTypeFromName(product.getCategory().getName());

            boolean inStock = switch (categoryType) {
                case PAINT -> checkStockForPaint(productOrder, product);
                case WALLPAPER -> checkStockForWallpaper(productOrder);
                case FLOOR -> checkStockForFloor(productOrder);
                default -> throw new IllegalArgumentException("Unknown category type: " + categoryType);
            };
            if (inStock) {
                reduceStock(productOrder, categoryType, product);
                productOrder.setSuccess(true);
            } else {
                productOrder.setSuccess(false);
            }
        }
        return products;
    }
    public String getCategoryByProductId(String productId) {
        Product product = productRepository.findByProductId(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getCategory().getName();
    }

    public boolean checkStockForPaint(ProductOrder productOrder, Product product) {
        Color color = colorRepository.findByColorId(productOrder.getColorId()).orElseThrow(()-> new ApiException("Color not found!"));
        Paint paint = paintRepository.findPaintByProductAndAndColor(product, color).orElseThrow(() -> new ApiException("Paint not found!"));
        Variant variant = variantRepository.findByVariantId(productOrder.getVariantId()).orElseThrow(()-> new ApiException("variant not found!"));
        PaintVariant paintVariant = paintVariantRepository.findByPaintAndVariant(paint, variant)
                .orElseThrow(() -> new RuntimeException("Paint variant not found"));

        return paintVariant.getQuantity() >= productOrder.getQuantity();
    }

    public boolean checkStockForWallpaper(ProductOrder productOrder) {
//        WallpaperVariant wallpaperVariant = wallpaperVariantRepository.findById(productOrder.getVariantId())
//                .orElseThrow(() -> new RuntimeException("Wallpaper variant not found"));
//
//        return wallpaperVariant.getQuantity() >= productOrder.getQuantity();
        return true;
    }

    public boolean checkStockForFloor(ProductOrder productOrder) {
//        FloorVariant floorVariant = floorVariantRepository.findById(productOrder.getVariantId())
//                .orElseThrow(() -> new RuntimeException("Floor variant not found"));
//
//        return floorVariant.getQuantity() >= productOrder.getQuantity();
        return true;
    }





    private ProductResponse mapToProductResponse(Product product) {
        return fromProductEntity(product);
    }


    public void reduceStock(ProductOrder productOrder, CategoryType categoryType, Product product) {
        switch (categoryType) {
            case PAINT:
                Color color = colorRepository.findByColorId(productOrder.getColorId()).orElseThrow(()-> new ApiException("Color not found!"));
                Paint paint = paintRepository.findPaintByProductAndAndColor(product, color).orElseThrow(() -> new ApiException("Paint not found!"));
                Variant variant = variantRepository.findByVariantId(productOrder.getVariantId()).orElseThrow(()-> new ApiException("variant not found!"));
                PaintVariant paintVariant = paintVariantRepository.findByPaintAndVariant(paint, variant)
                        .orElseThrow(() -> new RuntimeException("Paint variant not found"));

                paintVariant.setQuantity(paintVariant.getQuantity().intValue() - productOrder.getQuantity().intValue());
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
