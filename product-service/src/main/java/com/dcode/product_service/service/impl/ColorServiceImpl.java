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
    public void createColors(List<ColorRequest> colorRequests) {
        colorRequests.forEach(colorRequest -> {
            Color color = createAColorEntity(colorRequest);
            colorRepository.save(color);
        });
    }

    @Override
    public ColorResponse getAColor(String colorId) {
        var colorEntity = colorRepository.findByColorId(colorId).orElseThrow(() -> new ApiException("Color not found!"));
        return fromColorEntity(colorEntity);

    }

    @Override
    public void updateAColor(String colorId, ColorRequest colorRequest) {
        var color = colorRepository.findByColorId(colorId).orElseThrow(() -> new ApiException("Color not found!"));
        Color colorUpdated = updateColorEntity(color, colorRequest);
        colorRepository.save(colorUpdated);
    }

    @Override
    public void deleteAColor(String colorId) {
        colorRepository.deleteColorByColorId(colorId).orElseThrow(() -> new ApiException("Color not found!"));
    }

    @Override
    public PageResponse<ColorResponse> getAllColor(Pageable pageable) {
        var colors = colorRepository.findAll(pageable);
        if (colors.isEmpty()) throw new ApiException("Not found any!");
        Page<ColorResponse> colorResponsePage = colors.map(ColorUtils::fromColorEntity);
        return PageResponseBuilder.buildPageResponse(colorResponsePage);
    }

    @Override
    public PageResponse<ColorResponse> getColorByColorFamilyAndCollection(String collectionId, String colorFamilyId, Pageable pageable) {
        var colors = colorRepository.findByCollections_CollectionIdAndCollections_ColorFamily_ColorFamilyId(collectionId, colorFamilyId, pageable);
        if (colors.isEmpty())
            throw new ApiException("Not found any by collectionId: " + collectionId + " and color-familyId: " + colorFamilyId);
        Page<ColorResponse> colorResponsePage = colors.map(ColorUtils::fromColorEntityPartical);
        return PageResponseBuilder.buildPageResponse(colorResponsePage);
    }

    @Override
    public PageResponse<ColorResponse> getColorByCollectionAndRoom(String collectionId, String roomId, Pageable pageable) {
       var colors = colorRepository.findByCollections_CollectionIdAndCollections_Room_RoomId(collectionId, roomId, pageable);
       if (colors.isEmpty()) throw new ApiException("Not found any by collectionId " + collectionId + " and roomId: " + roomId);
       Page<ColorResponse> colorResponsePage = colors.map(ColorUtils::fromColorEntityPartical);
       return PageResponseBuilder.buildPageResponse(colorResponsePage);
    }

    @Override
    public PageResponse<ColorResponse> getColor(Boolean interior, Boolean exterior, Pageable pageable) {
        Page<Color> colors = Page.empty();
        if (Boolean.TRUE.equals(interior) && Boolean.TRUE.equals(exterior)) {
            colors = colorRepository.findByInteriorIsTrueAndExteriorIsTrue(pageable);
        } else if (Boolean.TRUE.equals(interior)) {
            colors = colorRepository.findByInteriorIsTrue(pageable);
        } else if (Boolean.TRUE.equals(exterior)) {
            colors = colorRepository.findByExteriorIsTrue(pageable);
        } else {
            throw  new ApiException("Color does not match the specified conditions");
        }
        Page<ColorResponse> colorResponsePage = colors.map(ColorUtils::fromColorEntityPartical);
        return PageResponseBuilder.buildPageResponse(colorResponsePage);
    }



    private Color createAColorEntity(ColorRequest colorRequest) {
        return createNewColorEntity(colorRequest);
    }
}
