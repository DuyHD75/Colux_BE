package com.dcode.product_service.service.impl;

import com.dcode.product_service.entity.RelativeCollection;
import com.dcode.product_service.repository.RelativeCollectionRepository;
import com.dcode.product_service.service.IRelativeCollectionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dcode.product_service.utils.RelativeCollectionUtils.createNewRelativeCollection;

@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class RelativeCollectionServiceImpl implements IRelativeCollectionService {

    private final RelativeCollectionRepository relativeCollectionRepository;


    @Override
    public void createARC(String name) {
        relativeCollectionRepository.save(createARCEntity(name));
    }

    private RelativeCollection createARCEntity(String name) {
        return createNewRelativeCollection(name);
    }
}
