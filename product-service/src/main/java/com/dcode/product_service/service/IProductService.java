package com.dcode.product_service.service;

import com.dcode.product_service.dto.PaintRequest;
import com.dcode.product_service.dto.PaintResponse;
import com.dcode.product_service.entity.Paint;

import java.util.List;

public interface IProductService {
    void createPaint(PaintRequest paintRequest);
    List<PaintResponse> getAllPaint();

}
