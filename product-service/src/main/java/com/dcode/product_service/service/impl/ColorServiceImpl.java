package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.ColorRequest;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.ColorRepository;
import com.dcode.product_service.service.IColorService;
import com.dcode.product_service.utils.ColorUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.ColorUtils.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ColorServiceImpl implements IColorService {

    private final ColorRepository colorRepository;

    @Override
    public void createAColor(ColorRequest colorRequest) {
        colorRepository.save(createAColorEntity(colorRequest));
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

    @Override
    public List<ColorResponse> getAllColor() {
        var colors = colorRepository.findAll();
        return colors.stream()
                .map(ColorUtils::fromColorEntityPartical)
                .toList();
    }

    @Override
    public PageResponse<ColorResponse> getColorByColorFamilyAndCollection(String collectionId, String colorFamilyId, Pageable pageable) {
        var colors = colorRepository.findByCollections_CollectionIdAndCollections_ColorFamily_ColorFamilyId(collectionId, colorFamilyId, pageable);
        if (colors.isEmpty()) throw new ApiException("Not found any by collectionId: " + collectionId + " and color-familyId: " + colorFamilyId);
        Page<ColorResponse> colorResponsePage = colors.map(ColorUtils::fromColorEntityPartical);
        return PageResponseBuilder.buildPageResponse(colorResponsePage);
    }

    private Color createAColorEntity(ColorRequest colorRequest) {
        return createNewColorEntity(colorRequest);
    }
}
