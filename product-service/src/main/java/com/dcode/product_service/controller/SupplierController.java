package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.SupplierRequest;
import com.dcode.product_service.exception.BusinessException;
import com.dcode.product_service.service.impl.SupplierServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/suppliers")
@AllArgsConstructor
@Slf4j
public class SupplierController {

    private final SupplierServiceImpl supplierService;

    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:create')")
    @PostMapping()
    public ResponseEntity<Response> createSupplier(@RequestBody SupplierRequest supplierRequest, HttpServletRequest request, HttpServletResponse response){
        try{
            supplierService.createSupplier(supplierRequest);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, emptyMap(), "Supplier created successfully", CREATED)
            );
        } catch (BusinessException ex) {
            return ResponseEntity.internalServerError().body(
                    getErrorResponse(request, response, ex, INTERNAL_SERVER_ERROR, Map.of("errorData", ex.getData()))
            );
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(
                    getErrorResponse(request, response, ex, INTERNAL_SERVER_ERROR, emptyMap())
            );
        }

    }
    @GetMapping("/public")
    public ResponseEntity<Response> getAllSupplier(HttpServletRequest request, HttpServletResponse response){
        try{
            var suppliers = supplierService.getAllSuppliers();
            return ResponseEntity.ok().body(
                    getResponse(request, Map.of("suppliers", suppliers), "Retrieve supplier successfully!", OK)
            );
        } catch (BusinessException ex) {
            return ResponseEntity.internalServerError().body(
                    getErrorResponse(request, response, ex, INTERNAL_SERVER_ERROR, Map.of("errorData", ex.getData()))
            );
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(
                    getErrorResponse(request, response, ex, INTERNAL_SERVER_ERROR, emptyMap())
            );
        }
    }

    private URI getUri() {
        return URI.create("");
    }
}
