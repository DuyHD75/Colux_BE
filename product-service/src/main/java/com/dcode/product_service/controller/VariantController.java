package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.VariantAttributeRequest;
import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.VariantResponse;
import com.dcode.product_service.service.impl.VariantServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/variants")
@AllArgsConstructor
public class VariantController {

    private final VariantServiceImpl variantService;

//    @RequestMapping("/paints")
//    public ResponseEntity<Response> getAllPaintVariant(HttpServletRequest request){
//       Set<VariantResponse> variantResponseSet = variantService.getAllPaintVariant();
//        return ResponseEntity.ok().body(getResponse(request, Map.of("variants", variantResponseSet),"Retrieve Paint Variant successfully!", OK));
//    }
    @PostMapping
    public ResponseEntity<Response> createAVariant(@RequestBody @Valid VariantAttributeRequest variantRequest, HttpServletRequest request){
        variantService.createAVariant(variantRequest.getSizeName(), variantRequest.getCategoryName(), variantRequest.getPackageType());
        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(), "Variant created successfully!", CREATED));

    }
    private URI getUri() {
        return URI.create("");
    }
}
