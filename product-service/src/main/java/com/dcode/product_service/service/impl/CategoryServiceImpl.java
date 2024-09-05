package com.dcode.product_service.service.impl;

import com.dcode.product_service.entity.Category;
import com.dcode.product_service.repository.CategoryRepository;
import com.dcode.product_service.service.ICategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.dcode.product_service.utils.CategoryUtils.createNewCategoryEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public void createCategory(String name, String thumbnail) {
        categoryRepository.save(createNewCategory(name, thumbnail));
    }

    private Category createNewCategory(String name, String thumbnail) {
        log.info(String.format("Creating new category: %s", name));
        return createNewCategoryEntity(name, thumbnail);
    }
}
