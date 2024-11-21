package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.PreorderRequest;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.service.impl.PreorderServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getErrorResponse;
import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/preorders")
@AllArgsConstructor
public class PreorderController {
    private final PreorderServiceImpl preorderService;

    @PostMapping("/public")
    public ResponseEntity<Response> createPreorder(@RequestBody @Valid PreorderRequest preorderRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            preorderService.createPreorder(preorderRequest);
            return ResponseEntity.created(getUri()).body(
                    getResponse(request, emptyMap(), "Preorder created successfully!", CREATED)
            );
        } catch (ApiException ex) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(getErrorResponse(request, response, ex, BAD_REQUEST));
        } catch (Exception exception) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(getErrorResponse(request, response, new ApiException("An unexpected error occurred."), INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/public/preorderId/{preorderId}")
    public ResponseEntity<Response> getAPreorder(@PathVariable("preorderId") String preorderId, HttpServletRequest request, HttpServletResponse response) {
        try {
        var preorder = preorderService.getAPreorder(preorderId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("preorder", preorder), "Preorder retrieve successfilly!", OK));
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
