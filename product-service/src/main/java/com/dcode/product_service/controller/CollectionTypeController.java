package com.dcode.product_service.controller;


import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.CollectionTypeRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.CollectionTypeServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/collectionTypes")
@AllArgsConstructor
public class CollectionTypeController {

    private final CollectionTypeServiceImpl collectionTypeService;

    @PreAuthorize("hasRole('EMPLOYEE') and hasAuthority('product:create')")
    @PostMapping
    public ResponseEntity<Response> createACT(@RequestBody @Valid CollectionTypeRequest CTRequest, HttpServletRequest request, HttpServletResponse response){
        try {
            collectionTypeService.createACT(CTRequest.getName());
            return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Collection Type created successfully!", CREATED));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }

    }
    private URI getUri(){
        return URI.create("");
    }

}
