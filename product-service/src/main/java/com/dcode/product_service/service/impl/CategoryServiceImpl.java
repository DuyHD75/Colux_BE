package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoResponse.CategoryResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.Category;
import com.dcode.product_service.entity.PageResponse;
import com.dcode.product_service.entity.PageResponseBuilder;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.CategoryRepository;
import com.dcode.product_service.repository.ProductRepository;
import com.dcode.product_service.service.ICategoryService;
import com.dcode.product_service.utils.CategoryUtils;
import com.dcode.product_service.utils.ProductUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dcode.product_service.utils.CategoryUtils.createNewCategoryEntity;
import static com.dcode.product_service.utils.CategoryUtils.fromCategoryEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final EntityManager entityManager;
    private final ProductServiceImpl productService;

    @Override
    public void createCategory(String name, String thumbnail) {
        categoryRepository.save(createNewCategory(name, thumbnail));
    }

    @Override
    public List<CategoryResponse> getAllCategory() {
        var categories = categoryRepository.findAll();
        if (categories.isEmpty()) throw new ApiException("Not found any category!");
        return categories.stream().map(
                CategoryUtils::fromCategoryEntity).toList();
    }

    @Override
    public CategoryResponse getCategoryByCategoryId(String categoryId) {
        var category = categoryRepository.findCategoryByCategoryId(categoryId).orElseThrow(() -> new ApiException("Category Id not found: " + categoryId));
        return fromCategoryEntity(category);
    }

    @Override
    public PageResponse<ProductResponse> getAllProductByCategoryId(String categoryId, Pageable page) {
        Category category = entityManager.unwrap(Session.class)
                .byNaturalId(Category.class)
                .using("categoryId", categoryId)
                .getReference();
        Page<Product> products = productRepository.findProductByCategory(category, page);
        if (products.isEmpty()) throw new ApiException("Product not found in this category!");
        Page<ProductResponse> productResponses = products.map(productService::mapToProductResponse);
        return PageResponseBuilder.buildPageResponse(productResponses);
    }

    private Category createNewCategory(String name, String thumbnail) {
        log.info(String.format("Creating new category: %s", name));
        return createNewCategoryEntity(name, thumbnail);
    }
}
