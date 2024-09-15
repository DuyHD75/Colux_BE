package com.dcode.product_service.repository;

import com.dcode.product_service.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface ColorRepository extends JpaRepository<Color, Long> {
    Optional<Color> findByColorId(String colorId);
    Optional<Color> deleteColorByColorId(String colorId);
    Set<Color>  findByColorIdIn(Set<String> colorIds);

}
