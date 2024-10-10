package com.dcode.product_service.service.impl;

import com.dcode.product_service.entity.CollectionType;
import com.dcode.product_service.repository.CollectionTypeRepository;
import com.dcode.product_service.service.ICollectionTypeService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dcode.product_service.utils.CollectionTypeUtils.createNewACT;

@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class CollectionTypeServiceImpl implements ICollectionTypeService {

    private final CollectionTypeRepository collectionTypeRepository;

    @Override
    public void createACT(String name) {
        collectionTypeRepository.save(createACTEntity(name));
    }

    private CollectionType createACTEntity(String name) {
        return createNewACT(name);
    }
}
