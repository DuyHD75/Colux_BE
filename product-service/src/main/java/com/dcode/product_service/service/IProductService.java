package com.dcode.product_service.service;

import com.dcode.product_service.dto.CartDto;
import com.dcode.product_service.dtoRequest.PreorderRequest;
import com.dcode.product_service.dtoRequest.ProductOrderRequest;
import com.dcode.product_service.dtoRequest.ProductRequest;
import com.dcode.product_service.dtoResponse.PreorderResponse;
import com.dcode.product_service.dtoResponse.ProductOrderResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface IProductService {
    //    void createPaint(PaintRequest paintRequest);
    void createProduct(ProductRequest productRequest);
//    List<PaintResponse> getAllPaint();

    List<ProductResponse> getAllProduct();

    List<ProductOrderResponse> purchaseOrder(List<ProductOrderRequest> products);

    PageResponse<ProductResponse> getAllProduct(Pageable pageable);

    List<CartDto> checkStockAvailability(List<ProductOrderRequest> productOrderRequestList);

//    void createProduct(ProductRequest productRequest);
}
