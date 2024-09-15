package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.ColorRequest;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.Color;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.ColorRepository;
import com.dcode.product_service.service.IColorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dcode.product_service.utils.ColorUtils.*;

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
    public ColorResponse getAColor(String colorId) {
        var colorEntity = colorRepository.findByColorId(colorId).orElseThrow(() -> new ApiException("Color not found!"));
        return fromColorEntity(colorEntity);

    }

    @Override
    public void updateAColor(String colorId, ColorRequest colorRequest) {
        var color = colorRepository.findByColorId(colorId).orElseThrow(()-> new ApiException("Color not found!"));
        Color colorUpdated = updateColorEntity(color, colorRequest);
        colorRepository.save(colorUpdated);
    }

    @Override
    public void deleteAColor(String colorId) {
        colorRepository.deleteColorByColorId(colorId).orElseThrow(()-> new ApiException("Color not found!"));
    }

    private Color createAColorEntity(String name, String code, String description) {
        return createNewColorEntity(name, code, description);
    }
}
