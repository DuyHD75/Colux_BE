package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoResponse.VariantResponse;
import com.dcode.product_service.entity.Variant;
import com.dcode.product_service.exception.BusinessException;
import com.dcode.product_service.repository.VariantRepository;
import com.dcode.product_service.service.IVariantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.dcode.product_service.utils.VariantUtils.fromAVariantEntity;
import static com.dcode.product_service.utils.VariantUtils.fromVariantEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class VariantServiceImpl implements IVariantService {

    private final VariantRepository variantRepository;

    @Override
    public void createAVariant(String sizeName, String categoryName, String packageType) {
        variantRepository.save(createAVariantEntity(sizeName, categoryName, packageType));
    }

    @Override
    public Set<VariantResponse> getAllVariant() {
        Set<Variant> variantSet = new HashSet<>(variantRepository.findAll());
        if (variantSet.isEmpty()) {
            throw new BusinessException("No variant found");
        }
        return fromVariantEntity(variantSet);
    }

    private Variant createAVariantEntity(String sizeName, String categoryName, String packageType) {
        return fromAVariantEntity(sizeName, categoryName, packageType);
    }

}
