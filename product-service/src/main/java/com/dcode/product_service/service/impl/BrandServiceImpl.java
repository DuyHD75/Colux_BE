package com.dcode.product_service.service.impl;

import com.dcode.product_service.entity.Brand;
import com.dcode.product_service.repository.BrandRepository;
import com.dcode.product_service.service.IBrandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.dcode.product_service.utils.BrandUtils.createNewBrandEntity;


@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements IBrandService {

    private final BrandRepository brandRepository;

    @Override
    public void createBrand(String name, String code, String status) {
        brandRepository.save(createNewBrand(name, code, status));
    }

    private Brand createNewBrand(String name, String code, String status) {
        log.info(String.format("Creating new brand: %s", name));
        return createNewBrandEntity(name, code, status);
    }
}
