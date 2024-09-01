package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.PaintRequest;
import com.dcode.product_service.dtoRequest.ProductOrder;
import com.dcode.product_service.dtoRequest.ProductRequest;
import com.dcode.product_service.dtoRequest.PurchaseOrderRequest;
import com.dcode.product_service.service.impl.ProductServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;

    @PostMapping("/purchase-order")
    public ResponseEntity<Response> purchaseOrder(@RequestBody @Valid PurchaseOrderRequest purchaseOrderRequest, HttpServletRequest request){
        List<ProductOrder> productOrderList = productService.purchaseOrder(purchaseOrderRequest.getProducts());
//        return ResponseEntity.ok().body(getResponse(request, Map.of("purchaseOrder", productOrderList),"Purchase Order handle successfully!", OK));

        boolean allSuccess = productOrderList.stream().allMatch(ProductOrder::isSuccess);

        if (allSuccess) {
            return ResponseEntity.ok().body(getResponse(request, Map.of("products", productOrderList), "Purchase Order handled successfully!", HttpStatus.OK));
        } else {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(getResponse(request, Map.of("products", productOrderList), "Some products could not be processed!", HttpStatus.PARTIAL_CONTENT));
        }
    }

    @PostMapping
    public ResponseEntity<Response> createProduct(@RequestBody @Valid ProductRequest productRequest, HttpServletRequest request) {
        productService.createProduct(
                productRequest.getDescription(), productRequest.getPlaceOfOrigin(),
                productRequest.getPrice(), productRequest.getProductName(),
                productRequest.getRatingAverage(), productRequest.getWarranty(),
                productRequest.getBrandId(), productRequest.getCategoryId()
        );
        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(),
                        "Product created successfully!", CREATED)
        );
    }

    @GetMapping
    public ResponseEntity<Response> getAllProduct(HttpServletRequest request) {
        var products = productService.getAllProduct();
        return ResponseEntity.ok().body(getResponse(request, Map.of("products", products), "Product info retrieved", OK));
    }


    private URI getUri() {
        return URI.create("");
    }

}
