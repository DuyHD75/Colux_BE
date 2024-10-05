package com.dcode.product_service.controller;

import com.dcode.product_service.domain.Response;
import com.dcode.product_service.dtoRequest.PaintRequest;
import com.dcode.product_service.service.impl.PaintServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.dcode.product_service.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/products/paints")
@AllArgsConstructor
public class PaintController {

    private final PaintServiceImpl paintService;

    @PostMapping("{productId}")
    public ResponseEntity<Response> createAPaint(@PathVariable("productId")String productId, @RequestBody @Valid PaintRequest paintRequest, HttpServletRequest request){
        paintService.createAPaint(productId, paintRequest.getColor(), paintRequest.getVariants());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Paint created successfully!", CREATED));
    }
    @GetMapping("{paintId}")
    public ResponseEntity<Response> getAPaint(@PathVariable("paintId")String paintId, HttpServletRequest request){
        var paint = paintService.getAPaint(paintId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("paint", paint), "Retrieve a paint successfully!", OK));
    }

    @PutMapping("{paintId}")
    public ResponseEntity<Response> updateAPaint(@PathVariable("paintId") String paintId, @RequestBody PaintRequest paintRequest, HttpServletRequest request){
        paintService.updateAPaint(paintId, paintRequest.getColor(), paintRequest.getVariants());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Paint update successfully!", HttpStatus.OK));
    }
    @DeleteMapping("{paintId}")
    public ResponseEntity<Response> deleteAPaint(@PathVariable("paintId")String paintId, HttpServletRequest request){
        paintService.deleteAPaint(paintId);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Delete Paint successfully!", OK));
    }

    @GetMapping
    public ResponseEntity<Response> getAllPaintPageable(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        HttpServletRequest request
                                                        ){
        Pageable pageable = PageRequest.of(page,size);
        var paints = paintService.getAllPaintPageable(pageable);
        return ResponseEntity.ok().body(getResponse(request, Map.of("paints", paints), "Paint retrieve successfully!", OK));
    }

    private URI getUri(){
        return URI.create("");
    }
}
