package com.dcode.product_service.utils;

import com.dcode.product_service.dto.ProductBrandDTO;
import com.dcode.product_service.dto.ProductCategoryDTO;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.Brand;
import com.dcode.product_service.entity.Category;
import com.dcode.product_service.entity.Product;
import org.springframework.beans.BeanUtils;

import java.util.UUID;
public class ProductUtils {

    public static Product createNewProductEntity(String description, String placeOfOrigin, String price, String productName, String ratingAverage, String warranty, Brand brand, Category category){
        return Product.builder()
                .productId(UUID.randomUUID().toString())
                .description(description)
                .placeOfOrigin(placeOfOrigin)
                .price(price)
                .productName(productName)
                .ratingAverage(ratingAverage)
                .warranty(warranty)
                .brand(brand)
                .category(category)
                .build();
    }
    public static ProductResponse fromProductEntity(Product product){
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        productResponse.setBrand(mapToProductBrandDTO(product.getBrand()));
        productResponse.setCategory(mapToProductCategoryDTO(product.getCategory()));
        return productResponse;
    }
    private static ProductCategoryDTO mapToProductCategoryDTO(Category category){
        if (category == null){
            return null;
        }
        return ProductCategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .thumbnail(category.getThumbnail())
                .build();
    }
    private static ProductBrandDTO mapToProductBrandDTO(Brand brand){
        if (brand == null){
            return null;
        }
        return ProductBrandDTO.builder()
                .brandId(brand.getBrandId())
                .name(brand.getName())
                .code(brand.getCode())
                .status(brand.getStatus())
        .build();
    }
}
