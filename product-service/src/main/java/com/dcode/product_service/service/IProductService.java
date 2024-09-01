package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.PaintRequest;
import com.dcode.product_service.dtoRequest.ProductOrder;
import com.dcode.product_service.dtoRequest.ProductRequest;
import com.dcode.product_service.dtoResponse.PaintResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;

import java.util.List;

public interface IProductService {
//    void createPaint(PaintRequest paintRequest);
    void createProduct(String description,
                   String placeOfOrigin,
                   String price,
                   String productName,
                   String ratingAverage,
                   String warranty,
                   String brandId,
                   String categoryId);
//    List<PaintResponse> getAllPaint();

    List<ProductResponse> getAllProduct();

    List<ProductOrder> purchaseOrder(List<ProductOrder> products);

//    void createProduct(ProductRequest productRequest);
}
