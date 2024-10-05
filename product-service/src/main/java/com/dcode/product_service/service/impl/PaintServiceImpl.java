package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.PaintResponse;
import com.dcode.product_service.entity.PageResponse;
import com.dcode.product_service.entity.PageResponseBuilder;
import com.dcode.product_service.entity.Paint;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.*;
import com.dcode.product_service.service.IPaintService;
import com.dcode.product_service.utils.PaintUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.dcode.product_service.utils.PaintUtils.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class PaintServiceImpl implements IPaintService {

    private final PaintRepository paintRepository;
    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final PaintVariantRepository paintVariantRepository;
    private final ColorRepository colorRepository;


    @Override
    public void updateAPaint(String paintId, String color, Set<VariantRequest> variantRequestSet) {
        var paint = paintRepository.findByPaintId(paintId).orElseThrow(() -> new ApiException("Paint not found!"));
        Set<String> variantIds = extractVariantIds(variantRequestSet);
        Paint paintUpdate = fromPaintEntity(color, checkVariantRequestSet(variantRequestSet, variantRepository.findAllByVariantIdIn(variantIds)), paint);
        paintRepository.save(paintUpdate);
    }


    public void createAPaint(String productId, String color, Set<VariantRequest> variantRequestSet) {
        paintRepository.save(createAPaintEntity(productId, color, variantRequestSet));
    }

    public PaintResponse getAPaint(String paintId) {
        var paintEntity = paintRepository.findByPaintId(paintId).orElseThrow(() -> new ApiException("Paint not found!"));
        return fromPaintEntity(paintEntity);
    }

    @Override
    public void deleteAPaint(String paintId) {
        var paint = paintRepository.findByPaintId(paintId).orElseThrow(() -> new ApiException("Paint not found!"));
        paintVariantRepository.deleteByPaint(paint);
        paintRepository.delete(paint);
    }

    @Override
    public PageResponse<PaintResponse> getAllPaintPageable(Pageable pageable) {
        var paints = paintRepository.findAll(pageable);
        if (paints.isEmpty()) throw new ApiException("Empty paint!");
        Page<PaintResponse> paintResponsePage = paints.map(PaintUtils::fromPaintEntity);
        return PageResponseBuilder.buildPageResponse(paintResponsePage);
    }

    private Paint createAPaintEntity(String productId, String color, Set<VariantRequest> variantRequestSet) {
        var product = productRepository.findByProductId(productId).orElseThrow(() -> new ApiException("Product not found!"));
        Set<String> variantIds = extractVariantIds(variantRequestSet);
        var colorEntity = colorRepository.findByColorId(color).orElseThrow(()-> new ApiException("ColorId not found!"));
        return createNewPaintEntity(product, colorEntity, checkVariantRequestSet(variantRequestSet, variantRepository.findAllByVariantIdIn(variantIds)));
    }


}
