package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.SupplierRequest;
import com.dcode.product_service.dtoResponse.SupplierResponse;
import com.dcode.product_service.entity.ProductSupplier;
import com.dcode.product_service.exception.BusinessException;
import com.dcode.product_service.repository.SupplierRepository;
import com.dcode.product_service.service.ISupplierService;
import com.dcode.product_service.utils.SupplierUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dcode.product_service.utils.SupplierUtils.fromSupplierEntity;
import static com.dcode.product_service.utils.SupplierUtils.fromSupplierEntityToResponse;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class SupplierServiceImpl implements ISupplierService {

    private final SupplierRepository supplierRepository;


    @Override
    public void createSupplier(SupplierRequest supplierRequest) {
        supplierRepository.save(createANewSupplier(supplierRequest));
    }

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        List<ProductSupplier> supplierResponseList = supplierRepository.findAll();
        if (supplierRepository.findAll().isEmpty()) {
            throw new BusinessException("No supplier found");
        }
        return supplierResponseList.stream().map(SupplierUtils::fromSupplierEntityToResponse).toList();
    }

    private ProductSupplier createANewSupplier(SupplierRequest supplierRequest) {
        return fromSupplierEntity(supplierRequest);
    }
}
