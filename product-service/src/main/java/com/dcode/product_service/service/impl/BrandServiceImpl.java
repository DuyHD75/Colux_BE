package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.BrandRequest;
import com.dcode.product_service.dtoResponse.BrandResponse;
import com.dcode.product_service.entity.Brand;
import com.dcode.product_service.exception.BusinessException;
import com.dcode.product_service.repository.BrandRepository;
import com.dcode.product_service.service.IBrandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.dcode.product_service.utils.BrandUtils.createNewBrandEntity;
import static com.dcode.product_service.utils.BrandUtils.fromEntityToResponse;


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

    @Override
    public Set<BrandResponse> getAllBrands() {
        Set<Brand> brands = new HashSet<>(brandRepository.findAll());
        if (brands.isEmpty()) {
            throw new BusinessException("No brands found.");
        }
        return fromEntityToResponse(brands);
    }

    @Override
    public void createBrands(List<BrandRequest> brandRequest) {
       brandRepository.saveAll(brandRequest.stream()
                .map(brand -> createNewBrand(brand.getName(), brand.getCode(), brand.getStatus()))
                .toList());
          }

    private Brand createNewBrand(String name, String code, String status) {
        log.info(String.format("Creating new brand: %s", name));
        return createNewBrandEntity(name, code, status);
    }
}
