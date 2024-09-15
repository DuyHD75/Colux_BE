package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.ColorFamilyRequest;
import com.dcode.product_service.service.impl.ColorFamilyServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@RequestMapping("/api/v1/products/colorFamilies")
@AllArgsConstructor
public class ColorFamilyController {

    private final ColorFamilyServiceImpl colorFamilyService;

    @PostMapping
    public ResponseEntity<Response> createAColorFamily(@RequestBody @Valid ColorFamilyRequest cfRequest, HttpServletRequest request){
        colorFamilyService.createACF(cfRequest.getName(), cfRequest.getDescription());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Color Family created successfully!", CREATED));
    }

    private URI getUri(){
        return URI.create("");
    }

}
