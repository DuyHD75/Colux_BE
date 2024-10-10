package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.PaintRequest;
import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.PaintResponse;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface IPaintService {
    void updateAPaint(String paintId, String color, Set<VariantRequest> variants);

    void createAPaint(String productId, String color, Set<VariantRequest> paintRequestSet);

    Object getAPaint(String paintId);

    void deleteAPaint(String paintId);

    PageResponse<PaintResponse> getAllPaintPageable(Pageable pageable);
}
