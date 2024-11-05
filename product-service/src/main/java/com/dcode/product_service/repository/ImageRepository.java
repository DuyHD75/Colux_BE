package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImageId(String imageId);
}
