package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.PreorderRequest;
import com.dcode.product_service.dtoResponse.PreorderResponse;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.entity.Variant;
import com.dcode.product_service.enumeration.CategoryType;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.PreorderRepository;
import com.dcode.product_service.repository.ProductRepository;
import com.dcode.product_service.service.IPreorderService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.dcode.product_service.utils.PreorderUtils.createNewPreorderEntity;
import static com.dcode.product_service.utils.PreorderUtils.fromPreorderEntity;
import static com.dcode.product_service.utils.ProductUtils.*;
import static com.dcode.product_service.utils.VariantUtils.fromVariantEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class PreorderServiceImpl implements IPreorderService {
    private final ProductRepository productRepository;
    private final ProductServiceImpl productServiceImpl;
    private final EntityManager entityManager;
    private final PreorderRepository preorderRepository;
    @Override
    public void createPreorder(PreorderRequest preorderRequest) {
        Product product = productRepository.findByProductId(preorderRequest.getProductId()).orElseThrow(() -> new ApiException("Product not found!"));
        CategoryType categoryType = productServiceImpl.getCategoryTypeFromName(product.getCategory().getName());

//        boolean inStock = switch (categoryType){
//            case PAINT -> productServiceImpl.checkStockForPaint(preorderRequest, product);
//            // Wallpaper and Floor
//            default -> throw new IllegalArgumentException("Unknown category type: " + categoryType);
//        };
//        if (inStock){
//            productServiceImpl.reduceStock(preorderRequest, categoryType, product);
//            Variant variant = entityManager.unwrap(Session.class)
//                    .byNaturalId(Variant.class)
//                    .using("variantId", preorderRequest.getVariantId())
//                    .getReference();
//            preorderRepository.save(createNewPreorderEntity(preorderRequest, product, variant));
//        }else throw new ApiException("Preorder out of stock!");
    }

    @Override
    public PreorderResponse getAPreorder(String preorderId) {
        var preorder = preorderRepository.findByPreorderId(preorderId).orElseThrow(() -> new ApiException("Preorder not found!"));
        var variantResponse = fromVariantEntity(Collections.singleton(preorder.getVariant()));
        PreorderResponse preorderResponse = fromPreorderEntity(preorder);
        preorderResponse.setVariant(variantResponse.stream().findFirst().orElseThrow(() -> new ApiException("Variant set is empty!")));
        preorderResponse.setProduct(fromProductEntity(preorder.getProduct()));
        return preorderResponse;
    }
}
