package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.PaintRequest;
import com.dcode.product_service.dtoRequest.VariantRequest;
import com.dcode.product_service.dtoResponse.PaintResponse;
import com.dcode.product_service.entity.Paint;
import com.dcode.product_service.entity.Variant;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.PaintRepository;
import com.dcode.product_service.repository.ProductRepository;
import com.dcode.product_service.repository.VariantRepository;
import com.dcode.product_service.service.IPaintService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
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


//    @Override
//    public void updateAPaint(String paintId, PaintRequest paintRequest) {
//        var paint = paintRepository.findByPaintId(paintId).orElseThrow(() -> new ApiException("Paint not found!"));
//
//        Map<String, Set<Variant>> result = getDeleteAndNewVariant(paint, paintRequest.getVariants());
//        Set<Variant> needToDelete = result.get("needToDelete");
//        Set<Variant> newVariants = result.get("newVariants");
//       Paint paintUpdate = fromPaintEntityAndIgnoreField(paintRequest, paint);
//       paintUpdate.setVariants(newVariants);
//        paintRepository.save(paintUpdate);
////        thảo luận lại cách 1 variant bị xóa
////        variantRepository.deleteAll(needToDelete);
//    }

    public void createAPaint(String productId, String quantity, String color, Set<String> variantRequestSet) {

        paintRepository.save(createAPaintEntity(productId, quantity, color, variantRequestSet));
    }

    public PaintResponse getAPaint(String paintId) {
         var paintEntity = paintRepository.findByPaintId(paintId).orElseThrow(()-> new ApiException("Paint not found!"));
         return fromPaintEntity(paintEntity);
    }


    private Paint createAPaintEntity(String productId, String quantity, String color, Set<String> variantRequestSet) {
        var product = productRepository.findByProductId(productId).orElseThrow(()-> new ApiException("Product not found!"));
        Set<Variant> variant = variantRepository.findAllByVariantIdIn(variantRequestSet);

        Set<String> foundIds = variant.stream()
                .map(Variant::getVariantId)
                .collect(Collectors.toSet());
        Set<String> notFoundIds = variantRequestSet.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());
        if (!notFoundIds.isEmpty()) {
            throw new ApiException("The following Variant IDs were not found: " + notFoundIds);
        }

        return createNewPaintEntity(product, quantity, color, variant);
    }

    private Map<String, Set<Variant>> getDeleteAndNewVariant(Paint paint, Set<VariantRequest> variants) {
        return updateVariants(paint, variants);
    }

}
