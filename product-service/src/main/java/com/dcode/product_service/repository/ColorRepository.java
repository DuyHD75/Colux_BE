package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Long> {
    Optional<Color> findByColorId(String colorId);
}
