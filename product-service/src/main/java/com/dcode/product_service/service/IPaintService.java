package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.PaintRequest;
import com.dcode.product_service.dtoResponse.PaintResponse;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

public interface IPaintService {
    void updateAPaint(String paintId, PaintRequest paintRequest);

    void createPaint(String productId, PaintRequest paintRequest);

    Object getAPaint(String paintId);

    void deleteAPaint(String paintId);

    PageResponse<PaintResponse> getAllPaintPageable(Pageable pageable);
}
