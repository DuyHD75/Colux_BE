package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.RelativeCollectionRequest;
import com.dcode.product_service.service.impl.RelativeCollectionServiceImpl;
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
import static org.springframework.cloud.client.DefaultServiceInstance.getUri;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/products/relativeCollections")
@AllArgsConstructor
public class RelativeCollectionController {

    private final RelativeCollectionServiceImpl relativeCollectionService;

    @PostMapping
    public ResponseEntity<Response> createARelativeCollection(@RequestBody @Valid RelativeCollectionRequest rcRequest, HttpServletRequest request){
        relativeCollectionService.createARC(rcRequest.getName());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Relative Collection created successfully!", CREATED));
    }
    private URI getUri(){
        return URI.create("");
    }

}
