package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.CollectionRequest;
import com.dcode.product_service.dtoResponse.CollectionResponse;
import com.dcode.product_service.dtoResponse.ColorResponse;
import com.dcode.product_service.entity.*;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.CollectionRepository;
import com.dcode.product_service.repository.ColorRepository;
import com.dcode.product_service.service.ICollectionService;
import com.dcode.product_service.utils.CollectionUtils;
import com.dcode.product_service.utils.ColorUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.dcode.product_service.utils.CollectionUtils.*;
import static com.dcode.product_service.utils.ColorUtils.fromColorEntity;

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
    public void createCollections(List<CollectionRequest> collectionRequests) {
        collectionRequests.forEach(collectionRequest -> {
            Collection collection = createACollectionEntity(collectionRequest);
            collectionRepository.save(collection);
        });
    }

    @Override
    public CollectionResponse getACollection(String collectionId) {
        var collection = collectionRepository.findByCollectionId(collectionId).orElseThrow(() -> new ApiException("Collection not found!"));
        return fromCollectionEntity(collection);
    }

    @Override
    public List<CollectionResponse> getAllCollection() {
        var collections = collectionRepository.findAll();
        return collections.stream()
                .map(CollectionUtils::fromCollectionBasicEntity)
                .toList();
    }

    @Override
    public PageResponse<ColorResponse> getColorByCollection(String collectionId, Pageable pageable) {
        var colors = colorRepository.findByCollections_CollectionId(collectionId, pageable);
        if (colors.isEmpty())
            throw new ApiException("No color found for collectionId: " + collectionId);

        Page<ColorResponse> colorResponsePage = colors.map(ColorUtils::fromColorEntityPartical);
        return PageResponseBuilder.buildPageResponse(colorResponsePage);

    }

    @Override
    public List<CollectionResponse> getAllCollectionWithoutColorFamilyAndRoom() {
        var collections = collectionRepository.findByColorFamilyIdIsNullAndRoomIdIsNull();
        if (collections.isEmpty()) throw new ApiException("Empty collection without color family - room!");
        return collections.stream().map(
                        CollectionUtils::fromCollectionEntity
                ).limit(5)
                .toList();
    }

    private Collection createACollectionEntity(CollectionRequest collectionRequest) {
        Set<Color> colorSet = getColorsFromNames(collectionRequest.getColors(), colorRepository.findByColorIdIn(collectionRequest.getColors()));

        // ColorFamily có thể là null, nếu không có ID
        ColorFamily colorFamily = Optional.ofNullable(collectionRequest.getColorFamilyId())
                .map(id -> entityManager.unwrap(Session.class)
                        .byNaturalId(ColorFamily.class)
                        .using("colorFamilyId", id)
                        .getReference())
                .orElse(null);  // Nếu null thì tiếp tục

        // Room có thể là null, nếu không có ID
        Room room = Optional.ofNullable(collectionRequest.getRoomId())
                .map(id -> entityManager.unwrap(Session.class)
                        .byNaturalId(Room.class)
                        .using("roomId", id)
                        .getReference())
                .orElse(null);  // Nếu null thì tiếp tục

        // CollectionType có thể là null, nếu không có ID
        CollectionType collectionType = Optional.ofNullable(collectionRequest.getCollectionTypeId())
                .map(id -> entityManager.unwrap(Session.class)
                        .byNaturalId(CollectionType.class)
                        .using("collectionTypeId", id)
                        .getReference())
                .orElse(null);  // Nếu null thì tiếp tục

        // RelativeCollection có thể là null, nếu không có ID
        RelativeCollection relativeCollection = Optional.ofNullable(collectionRequest.getRelativeCollectionId())
                .map(id -> entityManager.unwrap(Session.class)
                        .byNaturalId(RelativeCollection.class)
                        .using("relativeCollectionId", id)
                        .getReference())
                .orElse(null);  // Nếu null thì tiếp tục

        // Tạo thực thể Collection mới với các đối tượng có thể là null
        return createNewCollectionEntity(collectionRequest, colorSet, colorFamily, room, collectionType, relativeCollection);
    }



}
