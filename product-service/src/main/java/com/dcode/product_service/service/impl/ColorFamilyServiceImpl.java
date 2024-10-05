package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.dtoResponse.ColorFamilyResponse;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.Collection;
import com.dcode.product_service.entity.Color;
import com.dcode.product_service.entity.ColorFamily;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.ColorFamilyRepository;
import com.dcode.product_service.repository.ColorRepository;
import com.dcode.product_service.service.IColorFamilyService;
import com.dcode.product_service.utils.ColorFamilyUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ColorRepository colorRepository;

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

        return colorFamilyAll.stream()
                .map(ColorFamilyUtils::fromColorFamilyEntity)
                .toList();
    }

    @Override
    public List<CollectionResponse> getColorByColorFamily(String colorFamilyId) {
        var colorFamily = colorFamilyRepository.findByColorFamilyId(colorFamilyId).orElseThrow(() -> new ApiException("Color family Id not found: " + colorFamilyId));
        return colorFamily.getCollections().stream().map(
                collection -> {
                    var childColor = colorRepository.findByCollections_CollectionId(collection.getCollectionId());
                    if (childColor.isEmpty())
                        throw new ApiException("No color found for collectionId: " + collection.getCollectionId());
                    Set<ColorResponse> colorResponses = childColor.stream().map(color -> {
                        ColorResponse colorResponse = fromColorEntity(color);
                        colorResponse.setCollections(null);
                        colorResponse.setColorFamily(null);
                        return colorResponse;
                    }).collect(Collectors.toSet());

                    return CollectionResponse.builder()
                            .colors(colorResponses)
                            .collectionId(collection.getCollectionId())
                            .build();
                }
        ).toList();
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
