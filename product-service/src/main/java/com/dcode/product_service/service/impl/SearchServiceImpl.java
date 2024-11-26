package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.Color;
import com.dcode.product_service.entity.Paint;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.repository.ColorRepository;
import com.dcode.product_service.repository.ProductRepository;
import com.dcode.product_service.service.ISearchService;
import com.dcode.product_service.utils.ColorUtils;
import com.dcode.product_service.utils.ProductUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class SearchServiceImpl implements ISearchService {
    private ColorRepository colorRepository;

    private ProductRepository productRepository;

    public Map<String, List<?>> searchByKeyword(String keyword) {

        if (keyword.isBlank()) {
            return new HashMap<>();
        }
        List<Color> colors = colorRepository.searchColorsByKeyword(keyword);
        List<Product> productsInColor = colors.stream()
                .map(Color::getPaints)
                .flatMap(List::stream)
                .map(Paint::getProduct)
                .toList();

        if (!keyword.endsWith("*")){
            keyword += "*";
        }
        List<Product> products = productRepository.searchProductsByKeyword(keyword);

        Set<Product> uniqueProducts = new LinkedHashSet<>(productsInColor);
        uniqueProducts.addAll(products);

        List<ColorResponse> colorResponseList = colors.stream().map(ColorUtils::simpleColorResponse).toList();
        List<ProductResponse> productResponseList = uniqueProducts.stream()
                .filter(product -> (product.getPaints() != null && !product.getPaints().isEmpty()) ||
                        (product.getWallpapers() != null && !product.getWallpapers().isEmpty()) ||
                        (product.getFloors() != null && !product.getFloors().isEmpty()))
                .map(ProductUtils::fromProductEntity)
                .toList();


        Map<String, List<?>> results = new HashMap<>();
        results.put("colors", colorResponseList);
        results.put("products", productResponseList);

        return results;
    }

    @Override
    public Map<String, List<?>> bulkSearchByKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return new HashMap<>();
        }

        List<Product> products = productRepository.searchProductsByColors(keywords);

        List<ProductResponse> productResponseList = products.stream()
                .filter(product -> (product.getPaints() != null && !product.getPaints().isEmpty()) ||
                        (product.getWallpapers() != null && !product.getWallpapers().isEmpty()) ||
                        (product.getFloors() != null && !product.getFloors().isEmpty()))
                .map(ProductUtils::fromProductEntity)
                .toList();

        Map<String, List<?>> results = new HashMap<>();
        results.put("products", productResponseList);

        return results;
    }
}
