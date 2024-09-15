package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.CollectionRepository;
import com.dcode.product_service.repository.ColorRepository;
import com.dcode.product_service.service.ICollectionService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.dcode.product_service.utils.CollectionUtils.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class CollectionServiceImpl implements ICollectionService {
    @Autowired
    private EntityManager entityManager;
    private final CollectionRepository collectionRepository;
    private final ColorRepository colorRepository;

    @Override
    public void createACollection(String name, Set<String> colors, String colorFamilyId, String roomId, String collectionTypeId, String relativeCollectionId) {
        collectionRepository.save(createACollectionEntity(name, colors, colorFamilyId, roomId, collectionTypeId, relativeCollectionId));
    }

    @Override
    public CollectionResponse getACollection(String collectionId) {
        var collection = collectionRepository.findByCollectionId(collectionId).orElseThrow(()-> new ApiException("Collection not found!"));
        return fromCollectionEntity(collection);
    }

    private Collection createACollectionEntity(String name, Set<String> colors, String colorFamilyId, String roomId, String collectionTypeId, String relativeCollectionId) {
        Set<Color> colorSet = getColorsFromNames(colors, colorRepository.findByColorIdIn(colors));

        ColorFamily colorFamily = entityManager.unwrap(Session.class)
                .byNaturalId(ColorFamily.class)
                .using("ColorFamilyId", colorFamilyId)
                .getReference();

        Room room = entityManager.unwrap(Session.class)
                .byNaturalId(Room.class)
                .using("roomId", roomId)
                .getReference();

        CollectionType collectionType = entityManager.unwrap(Session.class)
                .byNaturalId(CollectionType.class)
                .using("collectionTypeId", collectionTypeId)
                .getReference();

        RelativeCollection relativeCollection = entityManager.unwrap(Session.class)
                .byNaturalId(RelativeCollection.class)
                .using("relativeCollectionId", relativeCollectionId)
                .getReference();

        return createNewCollectionEntity(name, colorSet, colorFamily, room, collectionType, relativeCollection);
    }




}
