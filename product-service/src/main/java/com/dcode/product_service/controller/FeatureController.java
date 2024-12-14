package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.FeatureRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.FeatureServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/features")
@AllArgsConstructor
@Validated
public class FeatureController {

    private final FeatureServiceImpl featureService;

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @PostMapping
    public ResponseEntity<Response> createFeatures(@RequestBody @Valid Set<FeatureRequest> featureRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            featureService.createFeatures(featureRequest);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, emptyMap(),
                            "Features created successfully!", CREATED)
            );
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @PutMapping("/featureId/{featureId}")
    public ResponseEntity<Response> updateFeature(@PathVariable("featureId") String featureId, @RequestBody @Valid FeatureRequest featureRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            featureService.updateFeature(featureRequest, featureId);
            return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Feature updated successfully!", OK));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public/featureId/{featureId}")
    public ResponseEntity<Response> getFeature(@PathVariable("featureId") String featureId, HttpServletRequest request, HttpServletResponse response) {
        try {
            var feature = featureService.getFeature(featureId);
            return ResponseEntity.ok().body(getResponse(request, Map.of("feature", feature), "Retrieve feature successfully!", OK));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<Response> getFeatures(HttpServletRequest request, HttpServletResponse response) {
        try {
            var features = featureService.getAllFeature();
            return ResponseEntity.ok().body(getResponse(request, Map.of("features", features), "Retrieve all features successfully!", OK));
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    private URI getUri() {
        return URI.create("");
    }

}
