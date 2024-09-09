package com.dcode.product_service.controller;


import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.ColorRequest;
import com.dcode.product_service.service.impl.ColorServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/colors")
@AllArgsConstructor
public class ColorController {

    private final ColorServiceImpl colorService;

    @PostMapping()
    public ResponseEntity<Response> createAColor(@RequestBody @Valid ColorRequest colorRequest, HttpServletRequest request){
        colorService.createAColor(colorRequest.getName(), colorRequest.getCode(), colorRequest.getDescription());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Color created successfully!", CREATED));
    }
    @GetMapping("{colorId}")
    public ResponseEntity<Response> getAColor(@PathVariable("colorId")String colorId, HttpServletRequest request){
        var color = colorService.getAColor(colorId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("color", color), "Color retrieve successfully!", OK));
    }

    private URI getUri(){
        return URI.create("");
    }

}
