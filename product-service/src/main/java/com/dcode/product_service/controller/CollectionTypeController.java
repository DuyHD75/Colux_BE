package com.dcode.product_service.controller;


import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.CollectionTypeRequest;
import com.dcode.product_service.service.impl.CollectionTypeServiceImpl;
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

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/products/collectionTypes")
@AllArgsConstructor
public class CollectionTypeController {

    private final CollectionTypeServiceImpl collectionTypeService;

    @PostMapping
    public ResponseEntity<Response> createACT(@RequestBody @Valid CollectionTypeRequest CTRequest, HttpServletRequest request){
        collectionTypeService.createACT(CTRequest.getName());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Collection Type created successfully!", CREATED));
    }
    private URI getUri(){
        return URI.create("");
    }

}
