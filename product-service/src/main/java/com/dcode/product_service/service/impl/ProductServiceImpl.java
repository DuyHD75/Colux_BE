package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.ProductOrder;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.*;
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

    public List<ProductOrder> purchaseOrder(List<ProductOrder> products) {
        for (ProductOrder productOrder : products) {
            boolean inStock = checkStock(productOrder); // Giả sử bạn có một phương thức để kiểm tra tồn kho
            if (inStock) {
                reduceStock(productOrder);
                productOrder.setSuccess(true);
            } else {
                productOrder.setSuccess(false);
            }
        }
        return products;
    }

    public boolean checkStock(ProductOrder productOrder) {
        switch (productOrder.getProductType()) {
            case "PAINT":
                return checkPaintStock(productOrder);
            case "WALLPAPER":
                return checkWallpaperStock(productOrder);
            case "FLOOR":
//                return checkFloorStock(productOrder);
            default:
                throw new ApiException("Invalid product type: " + productOrder.getProductType());
        }
    }


    public boolean checkPaintStock(ProductOrder productOrder) {
        Paint paint = paintRepository.findByPaintId(productOrder.getProductId())
                .orElseThrow(() -> new ApiException("Paint not found"));
        return paint.getQuantity() >= productOrder.getQuantity();
    }

    public boolean checkWallpaperStock(ProductOrder productOrder) {
        Wallpaper wallpaper = wallpaperRepository.findByWallpaperId(productOrder.getProductId())
                .orElseThrow(() -> new ApiException("Wallpaper not found"));
        return wallpaper.getArea() >= productOrder.getQuantity();
    }

//    public boolean checkFloorStock(ProductOrder productOrder) {
//        Floor floor = floorRepository.findById(productOrder.getProductId())
//                .orElseThrow(() -> new ApiException("Floor not found"));
//        return floor.getQuantity() >= productOrder.getQuantity();
//    }

    private ProductResponse mapToProductResponse(Product product) {
        return fromProductEntity(product);
    }

    public void reduceStock(ProductOrder productOrder) {
        switch (productOrder.getProductType()) {
            case "PAINT":
                reducePaintStock(productOrder);
                break;
            case "WALLPAPER":
                reduceWallpaperStock(productOrder);
                break;
            case "FLOOR":
//                reduceFloorStock(productOrder);
                break;
            default:
                throw new IllegalArgumentException("Invalid product type: " + productOrder.getProductType());
        }
    }

    private void reducePaintStock(ProductOrder productOrder) {
        Paint paint = paintRepository.findByPaintId(productOrder.getProductId())
                .orElseThrow(() -> new ApiException("Paint not found"));
        paint.setQuantity(paint.getQuantity() - productOrder.getQuantity());
        paintRepository.save(paint); // Lưu cập nhật vào cơ sở dữ liệu
    }

    private void reduceWallpaperStock(ProductOrder productOrder) {
        Wallpaper wallpaper = wallpaperRepository.findByWallpaperId(productOrder.getProductId())
                .orElseThrow(() -> new ApiException("Wallpaper not found"));
        wallpaper.setArea(wallpaper.getArea() - productOrder.getQuantity());
        wallpaperRepository.save(wallpaper); // Lưu cập nhật vào cơ sở dữ liệu
    }

//    private void reduceFloorStock(ProductOrder productOrder) {
//        Floor floor = floorRepository.findById(productOrder.getProductId())
//                .orElseThrow(() -> new ApiException("Floor not found"));
//        floor.setQuantity(floor.getQuantity() - productOrder.getQuantity());
//        floorRepository.save(floor); // Lưu cập nhật vào cơ sở dữ liệu
//    }

}
