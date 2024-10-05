package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Collection;
import com.dcode.product_service.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    Optional<Collection> findByCollectionId(String collectionId);
    List<Collection> findByColorFamilyIdIsNullAndRoomIdIsNull();
}
