package com.dcode.product_service.controller;


import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.ImageRequest;
import com.dcode.product_service.service.impl.ImageServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ImageController {

    private final ImageServiceImpl imageService;

    @PostMapping("/images")
    public ResponseEntity<Response> createAImage(@RequestBody @Valid ImageRequest imageRequest, HttpServletRequest request){

        imageService.createAImage(imageRequest);

        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(), "Image created successfully!", CREATED));
    }


    private URI getUri() {
        return URI.create("");
    }
}
