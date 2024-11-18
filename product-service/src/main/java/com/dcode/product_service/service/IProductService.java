package com.dcode.product_service.service;

import com.dcode.product_service.dto.CartDtoBase;
import com.dcode.product_service.dtoRequest.ProductOrderRequest;
import com.dcode.product_service.dtoRequest.ProductRequest;
import com.dcode.product_service.dtoRequest.ProductUpdateRequest;
import com.dcode.product_service.dtoResponse.ProductOrderResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.entity.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    //    void createPaint(PaintRequest paintRequest);
    void createProduct(ProductRequest productRequest);
//    List<PaintResponse> getAllPaint();

    List<ProductResponse> getAllProduct();

    List<ProductOrderResponse> purchaseOrder(List<ProductOrderRequest> products);

    PageResponse<ProductResponse> getAllProduct(Pageable pageable);

    List<CartDtoBase> checkStockAvailability(List<ProductOrderRequest> productOrderRequestList, boolean isBuildNameGHN);

    void updateProduct(ProductUpdateRequest productRequest);

    List<ProductResponse> getProductDashboard(List<ProductOrderRequest> productDashboardRequests);

    Object getDashboardInfo();

    ProductResponse getProductByProductId(String productId);


//    void createProduct(ProductRequest productRequest);
}
