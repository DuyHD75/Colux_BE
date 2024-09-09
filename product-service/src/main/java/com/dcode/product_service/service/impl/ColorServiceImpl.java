package com.dcode.product_service.service.impl;

import com.dcode.product_service.entity.Color;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.ColorRepository;
import com.dcode.product_service.service.IColorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dcode.product_service.utils.ColorUtils.createNewColorEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ColorServiceImpl implements IColorService {

    private final ColorRepository colorRepository;

    @Override
    public void createAColor(String name, String code, String description) {
        colorRepository.save(createAColorEntity(name, code, description));
    }

    @Override
    public Color getAColor(String colorId) {
        return colorRepository.findByColorId(colorId).orElseThrow(() -> new ApiException("Color not found!"));
    }

    private Color createAColorEntity(String name, String code, String description) {
        return createNewColorEntity(name, code, description);
    }
}
