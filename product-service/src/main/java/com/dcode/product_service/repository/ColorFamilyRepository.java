package com.dcode.product_service.repository;

import com.dcode.product_service.entity.ColorFamily;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ColorFamilyRepository extends JpaRepository<ColorFamily, Long> {
    Optional<ColorFamily> findByColorFamilyId(String colorFamilyId);
}
