package com.dcode.product_service.controller;

import com.dcode.product_service.dto.PaintRequest;
import com.dcode.product_service.dto.PaintResponse;
import com.dcode.product_service.entity.Paint;
import com.dcode.product_service.service.impl.ProductServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@AllArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;

    @GetMapping("/paint")
    @ResponseStatus(HttpStatus.OK)
    public List<PaintResponse> getAllPaint(){
        return productService.getAllPaint();
    }

    @PostMapping("/paint")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPaint(@RequestBody PaintRequest paintRequest){
        productService.createPaint(paintRequest);
    }

}
