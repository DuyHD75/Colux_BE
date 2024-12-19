package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.dtoResponse.ColorFamilyResponse;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.ColorFamilyRepository;
import com.dcode.product_service.repository.ColorRepository;
import com.dcode.product_service.service.IColorFamilyService;
import com.dcode.product_service.utils.ColorFamilyUtils;
import com.dcode.product_service.utils.ColorUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dcode.product_service.utils.ColorFamilyUtils.createNewACFEntity;
import static com.dcode.product_service.utils.ColorFamilyUtils.fromColorFamilyEntity;
import static com.dcode.product_service.utils.ColorUtils.fromColorEntity;


@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
public class ColorFamilyServiceImpl implements IColorFamilyService {
    @Autowired
    private EntityManager entityManager;
    private final ColorFamilyRepository colorFamilyRepository;
    private final ColorRepository colorRepository;

    @Override
    public void createACF(String name, String title, String description, String hex, String image) {
        colorFamilyRepository.save(createACFEntity(name, title, description, hex, image));
    }

    @Override
    public ColorFamilyResponse getAColorFamily(String colorFamilyId) {
        var colorFamilyEntity = colorFamilyRepository.findByColorFamilyId(colorFamilyId).orElseThrow(() -> new ApiException("Color Family not found!"));
        return fromColorFamilyEntity(colorFamilyEntity);
    }

    @Override
    public List<ColorFamilyResponse> getAllColorFamily() {
        var colorFamilyAll = colorFamilyRepository.findAll();
        if (colorFamilyAll.isEmpty()) throw new ApiException("Empty color family!");
        return colorFamilyAll.stream()
                .map(ColorFamilyUtils::fromColorFamilyEntity)
                .toList();
    }

    @Override
    public PageResponse<ColorResponse> getColorByColorFamily(String colorFamilyId, Pageable pageable) {
        var colors = colorRepository.findByCollections_ColorFamily_ColorFamilyId(colorFamilyId, pageable);
        if (colors.isEmpty()) throw new ApiException("Not found any!");

        Page<ColorResponse> colorResponsePage = colors.map(ColorUtils::fromColorEntityPartical);
        return PageResponseBuilder.buildPageResponse(colorResponsePage);
    }


    private ColorFamily createACFEntity(String name, String title, String description, String hex, String image) {
//        Set<Collection> collectionSet = collectionIds.stream()
//                .map(collectionId -> entityManager.unwrap(Session.class)
//                        .byNaturalId(Collection.class)
//                        .using("collectionId", collectionId)
//                        .getReference())
//                .collect(Collectors.toSet());
        return createNewACFEntity(name, title, description, hex, image);
    }
}
