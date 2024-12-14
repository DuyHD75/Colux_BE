package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.VariantAttributeRequest;
import com.dcode.product_service.dtoResponse.VariantResponse;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.exception.BusinessException;
import com.dcode.product_service.service.impl.VariantServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/variants")
@AllArgsConstructor
public class VariantController {

    private final VariantServiceImpl variantService;

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @PostMapping
    public ResponseEntity<Response> createAVariant(@RequestBody @Valid VariantAttributeRequest variantRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            variantService.createAVariant(variantRequest.getSizeName(), variantRequest.getCategoryName(), variantRequest.getPackageType());
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, emptyMap(), "Variant created successfully!", CREATED));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }

    @GetMapping("/public")
    public ResponseEntity<Response> getAllVariant(HttpServletRequest request, HttpServletResponse response) {
        try {
            Set<VariantResponse> variantResponseSet = variantService.getAllVariant();
            return ResponseEntity.ok().body(getResponse(request, Map.of("variants", variantResponseSet), "Retrieve Variant successfully!", OK));
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
