package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.PaintRequest;
import com.dcode.product_service.dtoRequest.VariantRequest;

import java.util.Set;

public interface IPaintService {
//    void updateAPaint(String paintId, PaintRequest paintRequest);

    void createAPaint(String productId, String quantity, String color, Set<String> paintRequestSet);

    Object getAPaint(String paintId);
}
