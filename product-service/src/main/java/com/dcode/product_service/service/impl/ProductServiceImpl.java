package com.dcode.product_service.service.impl;

import com.dcode.product_service.dto.PaintRequest;
import com.dcode.product_service.dto.PaintResponse;
import com.dcode.product_service.entity.Paint;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.repository.PaintRepository;
import com.dcode.product_service.service.IProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService {

    private final PaintRepository paintRepository;


    public List<PaintResponse> getAllPaint() {
        List<Paint> paints = paintRepository.findAll();

        return paints.stream().map(this::mapToPaintResponse).toList();
    }

    private PaintResponse mapToPaintResponse(Paint paint) {
        return PaintResponse.builder()
                .size(paint.getSize())
                .warranty(paint.getWarranty())
                .ratingAverage(paint.getRatingAverage())
                .productName(paint.getProductName())
                .price(paint.getPrice())
                .placeOfOrigin(paint.getPlaceOfOrigin())
                .detail(paint.getDetail())
                .description(paint.getDescription())
                .category(paint.getCategory())
                .build();
    }

    public void createPaint(PaintRequest paintRequest) {
        Paint paint = new Paint();
        paint.setCategory(paintRequest.getCategory());
        paint.setDescription(paintRequest.getDescription());
        paint.setDetail(paintRequest.getDetail());
        paint.setPlaceOfOrigin(paintRequest.getPlaceOfOrigin());
        paint.setPrice(paintRequest.getPrice());
        paint.setProductName(paintRequest.getProductName());
        paint.setRatingAverage(paintRequest.getRatingAverage());
        paint.setWarranty(paintRequest.getWarranty());
        paint.setSize(paintRequest.getSize());


        log.info("Paint {} is saved", paint);
        paintRepository.save(paint);
    }


}
