package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.RequestProperty;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.PropertyServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/products/properties")
@AllArgsConstructor
public class PropertyController {
    private final PropertyServiceImpl propertyService;

    @PostMapping()
    public ResponseEntity<Response> createProperty(@RequestBody @Valid RequestProperty requestProperty, HttpServletRequest request, HttpServletResponse response) {
        try {
        propertyService.createAProperty(requestProperty.getName(), requestProperty.getDescription(), requestProperty.getPropertyValues());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(),"Property created successfully!", HttpStatus.CREATED));
        }catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<Response> getAProperty(@PathVariable("propertyId") String propertyId, HttpServletRequest request, HttpServletResponse response){
        try {
        var property = propertyService.getAProperty(propertyId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("property", property),"Retrieve property successfully!", HttpStatus.OK));
        }catch (ApiException ex) {
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


