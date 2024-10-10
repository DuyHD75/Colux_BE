package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.ColorFamilyRequest;
import com.dcode.product_service.service.impl.ColorFamilyServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/products/colorFamilies")
@AllArgsConstructor
public class ColorFamilyController {

    private final ColorFamilyServiceImpl colorFamilyService;

    @PostMapping
    public ResponseEntity<Response> createAColorFamily(@RequestBody @Valid ColorFamilyRequest cfRequest, HttpServletRequest request) {
        colorFamilyService.createACF(cfRequest.getName(), cfRequest.getTitle(), cfRequest.getDescription(), cfRequest.getHex(), cfRequest.getImage());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Color Family created successfully!", CREATED));
    }

    @GetMapping("/{colorFamilyId}")
    public ResponseEntity<Response> getAColorFamily(@PathVariable("colorFamilyId") String colorFamilyId, HttpServletRequest request) {
        var colorFamily = colorFamilyService.getAColorFamily(colorFamilyId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("colorFamily", colorFamily), "Color family retrieve successfully", OK));
    }

    @GetMapping("/{colorFamilyId}/colors")
    public ResponseEntity<Response> getColorByColorFamily(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @PathVariable("colorFamilyId") String colorFamilyId, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        var colors = colorFamilyService.getColorByColorFamily(colorFamilyId, pageable);
        return ResponseEntity.ok().body(getResponse(request, Map.of("collections", colors), "Color retrieve successfully!", OK));
    }

    @GetMapping
    public ResponseEntity<Response> getAllColorFamily(HttpServletRequest request) {
        var colorFamilies = colorFamilyService.getAllColorFamily();
        return ResponseEntity.ok().body(getResponse(request, Map.of("colorFalimies", colorFamilies), "All Color family retrieve successfully!", OK));
    }

    private URI getUri() {
        return URI.create("");
    }

}
