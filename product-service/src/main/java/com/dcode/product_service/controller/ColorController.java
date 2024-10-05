package com.dcode.product_service.controller;


import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.ColorRequest;
import com.dcode.product_service.service.impl.ColorServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/v1/products/colors")
@AllArgsConstructor
public class ColorController {

    private final ColorServiceImpl colorService;

    @PostMapping()
    public ResponseEntity<Response> createAColor(@RequestBody @Valid ColorRequest colorRequest, HttpServletRequest request) {
        colorService.createAColor(colorRequest);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Color created successfully!", CREATED));
    }

    @GetMapping("{colorId}")
    public ResponseEntity<Response> getAColor(@PathVariable("colorId") String colorId, HttpServletRequest request) {
        var color = colorService.getAColor(colorId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("color", color), "Color retrieve successfully!", OK));
    }

    @PutMapping("{colorId}")
    public ResponseEntity<Response> updateAColor(@PathVariable("colorId") String colorId, @RequestBody ColorRequest colorRequest, HttpServletRequest request) {
        colorService.updateAColor(colorId, colorRequest);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Color update successfully!", OK));
    }

    @DeleteMapping("{colorId}")
    public ResponseEntity<Response> deleteAColor(@PathVariable("colorId") String colorId, HttpServletRequest request) {
        colorService.deleteAColor(colorId);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Color deleted successfully!", OK));
    }

    @GetMapping
    public ResponseEntity<Response> getAllColor(HttpServletRequest request) {
        var colors = colorService.getAllColor();
        return ResponseEntity.ok().body(getResponse(request, Map.of("colors", colors), "Retrieve all color successfully", OK));
    }

    @GetMapping("/color-family/{colorFamilyId}/collection/{collectionId}")
    public ResponseEntity<Response> getColorByColorFamilyAndCollection(@PathVariable("colorFamilyId") String colorFamilyId,
                                                                       @PathVariable("collectionId") String collectionId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        var colors = colorService.getColorByColorFamilyAndCollection(collectionId, colorFamilyId, pageable);
        return ResponseEntity.ok().body(getResponse(request, Map.of("colors", colors), "Retrieve colors by color family and collection successfully!", OK));
    }

    private URI getUri() {
        return URI.create("");
    }

}
