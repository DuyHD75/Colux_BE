package com.dcode.product_service.service.impl;

import com.dcode.product_service.entity.ColorFamily;
import com.dcode.product_service.repository.ColorFamilyRepository;
import com.dcode.product_service.service.IColorFamilyService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dcode.product_service.utils.ColorFamilyUtils.createNewACFEntity;


@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class ColorFamilyServiceImpl implements IColorFamilyService {

    private final ColorFamilyRepository colorFamilyRepository;

    @Override
    public void createACF(String name, String description) {
        colorFamilyRepository.save(createACFEntity(name, description));
    }

    private ColorFamily createACFEntity(String name, String description) {
        return createNewACFEntity(name, description);
    }
}
