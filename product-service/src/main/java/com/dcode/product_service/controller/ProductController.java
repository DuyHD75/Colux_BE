package com.dcode.product_service.controller;

import com.dcode.product_service.domain.ArrayResponse;
import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dto.CartDtoBase;
import com.dcode.product_service.dtoRequest.*;
import com.dcode.product_service.dtoRequest.order_service.OrderLineDTO;
import com.dcode.product_service.dtoResponse.ProductOrderResponse;
import com.dcode.product_service.dtoResponse.ProductResponse;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.exception.BusinessException;
import com.dcode.product_service.service.impl.ProductServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @PostMapping("/purchase-order")
    public ResponseEntity<ArrayResponse> purchaseOrder(@RequestBody @Valid List<ProductOrderRequest> productOrderRequestList, HttpServletRequest request, HttpServletResponse response) {
        List<ProductOrderResponse> productOrderResponseList;
        try {
            productOrderResponseList = productService.purchaseOrder(productOrderRequestList);
        } catch (ApiException ex) {
            log.error("API exception occurred: ", ex); // Ghi log lỗi
            productOrderResponseList = ex.getOrderResponses(); // Lấy danh sách phản hồi từ ngoại lệ
        }

        // Kiểm tra xem có sản phẩm nào không thành công hay không
        boolean allSuccess = productOrderResponseList.stream().allMatch(ProductOrderResponse::isSuccess);

        if (allSuccess) {
            return ResponseEntity.ok().body(new ArrayResponse(
                    request.getRequestURI(),
                    "Purchase Order handled successfully!",
                    HttpStatus.OK,
                    productOrderResponseList
//                    Map.of("products", productOrderResponseList),
//                    "Purchase Order handled successfully!",
//                    HttpStatus.OK
            ));
        } else {
            return ResponseEntity.status(
                    BAD_REQUEST).body(new ArrayResponse(
                    request.getRequestURI(),
                    "Some products could not be processed!",
                    BAD_REQUEST,
                    productOrderResponseList
//                    HttpStatus.OK).body(getResponse(request,
//                    Map.of("products", productOrderResponseList),
//                    "Some products could not be processed!",
//                    HttpStatus.OK
            ));
        }
    }

    @PostMapping("/getProductByVariant")
    public ResponseEntity<Response> cartVariant(@RequestBody @Valid List<ProductOrderRequest> productOrderRequestList, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<CartDtoBase> productCartResponses = productService.checkStockAvailability(productOrderRequestList, false);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, Map.of("products", productCartResponses),
                            "Retrieve products successfully!", CREATED)
            );
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }

    @PostMapping("/getInfo")
    public ResponseEntity<Response> getInfoForBuildNameGHN(@RequestBody @Valid List<ProductOrderRequest> productOrderRequestList, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<CartDtoBase> productCartResponses = productService.checkStockAvailability(productOrderRequestList, true);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, Map.of("products", productCartResponses),
                            "Retrieve products successfully!", CREATED)
            );
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/getProductDashboard")
    public ResponseEntity<Response> getProductDashboard(@RequestBody @Valid List<ProductOrderRequest> productDashboardRequests, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<ProductResponse> productCartResponses = productService.getProductDashboard(productDashboardRequests);
            return ResponseEntity.ok().body(
                    getResponse(request, Map.of("products", productCartResponses),
                            "Retrieve products successfully!", CREATED)
            );
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/reduceProduct")
    public ResponseEntity<Response> reduceProduct(@RequestBody @Valid List<OrderLineDTO> orderLineDTOS, HttpServletRequest request, HttpServletResponse response) {
        try {
            String productCartResponses = productService.orderCancelRestore(orderLineDTOS);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, Map.of("products", productCartResponses),
                            "Retrieve products successfully!", CREATED)
            );
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }

    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:create')")
    @PostMapping
    public ResponseEntity<Response> createProduct(@RequestBody @Valid ProductRequest productRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            productService.createProduct(productRequest);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, emptyMap(),
                            "Product created successfully!", CREATED)
            );
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:create')")
    @PostMapping("/bulk")
    public ResponseEntity<Response> createProducts(@RequestBody @Valid Set<ProductRequest> productRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            productService.createProducts(productRequest);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, emptyMap(),
                            "Product created successfully!", CREATED)
            );
        } catch (ApiException ex) {
            log.error("here: ", ex);
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:create')")
    @PostMapping("/excel")
    public ResponseEntity<Response> createProductsFromExcel(@RequestBody Set<ProductExcelRequest> productRequest, HttpServletRequest request,
                                                            HttpServletResponse response) {
        try {
            productService.saveProductsFromExcel(productRequest);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, emptyMap(),
                            "Product save successfully!", CREATED)
            );
        } catch (BusinessException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<Response> getAllProduct(HttpServletRequest request, HttpServletResponse response) {
        try {
            var products = productService.getAllProduct();
            return ResponseEntity.ok().body(getResponse(request, Map.of("products", products), "Product info retrieved", OK));
        } catch (BusinessException ex) {
            return ResponseEntity.status(BAD_REQUEST).body(
                    getErrorResponse(request, response, ex, BAD_REQUEST, emptyMap())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    getErrorResponse(request, response, ex, INTERNAL_SERVER_ERROR, emptyMap())
            );
        }
    }

    @GetMapping("/public/productId/{productId}")
    public ResponseEntity<Response> getProductByProductId(@PathVariable String productId, HttpServletRequest request, HttpServletResponse response) {
        try {
            var product = productService.getProductByProductId(productId);
            return ResponseEntity.ok().body(getResponse(request, Map.of("product", product), "Product info retrieved", OK));
        } catch (BusinessException ex) {
            return ResponseEntity.status(BAD_REQUEST).body(
                    getErrorResponse(request, response, ex, BAD_REQUEST, emptyMap())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    getErrorResponse(request, response, ex, INTERNAL_SERVER_ERROR, emptyMap())
            );
        }
    }

    @GetMapping("/public/pageable")
    public ResponseEntity<Response> getAllProductWithPagination(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                HttpServletRequest request,
                                                                HttpServletResponse response) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            var products = productService.getAllProduct(pageable);

            return ResponseEntity.ok().body(getResponse(request, Map.of("products", products), "Product retrieve successfully!", OK));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:update')")
    @PutMapping
    public ResponseEntity<Response> updateProduct(@RequestBody ProductUpdateRequest productRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            productService.updateProduct(productRequest);
            return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Product updated successfully!", OK));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }


    @GetMapping("/public/filter")
    public ResponseEntity<Response> getAllProductWithFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) List<String> features,
            @RequestParam(required = false) List<String> properties,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            HttpServletResponse response,
            HttpServletRequest request) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            var filteredProducts = productService.filterProducts(type, features, properties, minPrice, maxPrice, pageable);
            return ResponseEntity.ok().body(getResponse(request, Map.of("products", filteredProducts), "Product retrieve successfully!", OK));
        } catch (ApiException ex) {
            log.error("bad-request: ", ex);
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            log.error("internal: ", exception);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }

    @GetMapping("/getDashboardInfo")
    public ResponseEntity<Response> getDashboardInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            var dashboardInfo = productService.getDashboardInfo();
            return ResponseEntity.ok().body(getResponse(request, Map.of("dashboard", dashboardInfo), "Dashboard info retrieved", OK));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    private URI getUri() {
        return URI.create("");
    }

}
