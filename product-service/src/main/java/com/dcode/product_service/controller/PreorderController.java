package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.PreorderRequest;
import com.dcode.product_service.service.impl.PreorderServiceImpl;
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
@RequestMapping("/api/v1/products/preorders")
@AllArgsConstructor
public class PreorderController {
    private final PreorderServiceImpl preorderService;

    @PostMapping("")
    public ResponseEntity<Response> createPreorder(@RequestBody @Valid PreorderRequest preorderRequest, HttpServletRequest request){
        preorderService.createPreorder(preorderRequest);
        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(), "Preorder created successfully!", CREATED)
        );
    }
    @GetMapping("/{preorderId}")
    public ResponseEntity<Response> getAPreorder(@PathVariable("preorderId")String preorderId, HttpServletRequest request){
        var preorder = preorderService.getAPreorder(preorderId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("preorder", preorder), "Preorder retrieve successfilly!", OK));
    }

    private URI getUri() {
        return URI.create("");
    }
}
