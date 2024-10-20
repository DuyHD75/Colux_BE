package com.dcode.product_service.service.impl;

import com.dcode.product_service.dtoRequest.ImageRequest;
import com.dcode.product_service.entity.Image;
import com.dcode.product_service.entity.Product;
import com.dcode.product_service.exception.ApiException;
import com.dcode.product_service.repository.ImageRepository;
import com.dcode.product_service.repository.ProductRepository;
import com.dcode.product_service.service.IImageService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import static com.dcode.product_service.utils.ImageUtils.createNewImageEntity;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements IImageService {

    private final ImageRepository imageRepository;
    private final EntityManager entityManager;
    private final ProductRepository productRepository;
    @Override
    public void createAImage(ImageRequest imageRequest) {
        imageRepository.save(createANewImage(imageRequest));
    }

    private Image createANewImage(ImageRequest imageRequest) {
//        Product product = entityManager.unwrap(Session.class)
//                .byNaturalId(Product.class)
//                .using("productId", imageRequest.getProductId())
//                .getReference();
        Product product = productRepository.findByProductId(imageRequest.getProductId()).orElseThrow(() -> new ApiException("Product not found!"));
        return createNewImageEntity(imageRequest, product);
    }
}
