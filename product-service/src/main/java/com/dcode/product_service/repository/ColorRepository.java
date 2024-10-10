package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ColorRepository extends JpaRepository<Color, Long> {
    Optional<Color> findByColorId(String colorId);
    Optional<Color> deleteColorByColorId(String colorId);
    Set<Color>  findByColorIdIn(Set<String> colorIds);
    Page<Color> findByCollections_ColorFamily_ColorFamilyId(String collectionId, Pageable pageable);
    Page<Color> findByCollections_CollectionIdAndCollections_ColorFamily_ColorFamilyId(String collectionId, String colorId, Pageable pageable);
    Page<Color> findByCollections_CollectionIdAndCollections_Room_RoomId(String collectionId, String roomId, Pageable pageable);
    Page<Color> findByCollections_Room_RoomId(String roomId, Pageable pageable);
    Page<Color> findByCollections_CollectionId(String collectionId, Pageable pageable);
    Page<Color> findByInteriorIsTrue(Pageable pageable);
    Page<Color> findByInteriorIsTrueAndExteriorIsTrue(Pageable pageable);
    Page<Color> findByExteriorIsTrue(Pageable pageable);
}
