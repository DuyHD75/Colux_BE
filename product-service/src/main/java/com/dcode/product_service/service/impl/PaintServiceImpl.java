package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.PaintRequest;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public void updateAPaint(String paintId, PaintRequest paintRequest) {
        // chua co update color
        var paint = paintRepository.findByPaintId(paintId).orElseThrow(() -> new ApiException("Paint not found!"));
        Set<String> variantIds = extractVariantIds(paintRequest.getVariants());
        Paint paintUpdate = fromPaintEntity(paintRequest.getColor(), checkVariantRequestSet(paintRequest.getVariants(), variantRepository.findAllByVariantIdIn(variantIds)), paint);
        paintRepository.save(paintUpdate);
    }


    public void createPaint(String productId, PaintRequest paintRequest) {
        try {
            paintRepository.save(createAPaintEntity(productId, paintRequest));
        } catch (DataIntegrityViolationException e) {
            Throwable cause = e.getRootCause();
            if (cause instanceof SQLIntegrityConstraintViolationException) {
                // Customize the error message based on the input data
                String errorMessage = String.format("Color with name '%s' already exists for product ID: %s!",
                        paintRequest.getColor(), productId);
                throw new ApiException(errorMessage, e);
            }
            throw e; // Rethrow if it's not the expected exception
        }
    }

    public void createPaints(Set<PaintRequest> paintRequests) {
        paintRepository.saveAll(paintRequests.stream().map(
                paintRequest -> createAPaintEntity(paintRequest.getProductId(), paintRequest)).collect(Collectors.toSet())
        );
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

    private Paint createAPaintEntity(String productId, PaintRequest paintRequest) {
        var product = productRepository.findByProductId(productId).orElseThrow(() -> new ApiException("Product not found!"));
        Set<String> variantIds = extractVariantIds(paintRequest.getVariants());
        var colorEntity = colorRepository.findByColorId(paintRequest.getColor()).orElseThrow(()-> new ApiException("ColorId not found!"));
        return createNewPaintEntity(product, colorEntity, checkVariantRequestSet(paintRequest.getVariants(), variantRepository.findAllByVariantIdIn(variantIds)));
    }


}
