package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
