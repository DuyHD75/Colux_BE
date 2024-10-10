package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.FeatureRequest;
import com.dcode.product_service.service.impl.FeatureServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/products/features")
@AllArgsConstructor
public class FeatureController {

    private final FeatureServiceImpl featureService;

    @PostMapping
    public ResponseEntity<Response> createFeature(@RequestBody @Valid FeatureRequest featureRequest, HttpServletRequest request){
        featureService.createFeature(featureRequest.getName(), featureRequest.getDescription(), featureRequest.getFeatureValue());
        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(),
                        "Features created successfully!", CREATED)
        );
    }
    @PutMapping("/{featureId}")
    public ResponseEntity<Response> updateFeature(@PathVariable("featureId") String featureId, @RequestBody @Valid FeatureRequest featureRequest, HttpServletRequest request){
        featureService.updateFeature(featureRequest.getName(), featureRequest.getDescription(), featureRequest.getFeatureValue(), featureId);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Feature updated successfully!", OK));
    }

    @GetMapping("/{featureId}")
    public ResponseEntity<Response> getFeature(@PathVariable("featureId") String featureId, HttpServletRequest request){
        var feature = featureService.getFeature(featureId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("feature", feature), "Retrieve feature successfully!",OK));
    }
    private URI getUri() {
        return URI.create("");
    }

}
