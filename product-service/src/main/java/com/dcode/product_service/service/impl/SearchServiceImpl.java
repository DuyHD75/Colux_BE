package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.Color;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Product> products = productRepository.searchProductsByKeyword(keyword);

        List<ColorResponse> colorResponseList = colors.stream().map(ColorUtils::simpleColorResponse).toList();
        List<ProductResponse> productResponseList = products.stream().map(
                product -> {
                    if (product.getPaints() == null && product.getWallpapers() == null && product.getFloors() == null) {
                        return null;
                    }
                    return ProductUtils.fromProductEntity(product);
                }).toList();

        Map<String, List<?>> results = new HashMap<>();
        results.put("colors", colorResponseList);
        results.put("products", productResponseList);

        return results;
    }

}
