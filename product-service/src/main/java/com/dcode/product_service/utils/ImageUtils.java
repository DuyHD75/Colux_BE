package com.dcode.product_service.utils;

import com.dcode.product_service.dtoRequest.ImageRequest;
import com.dcode.product_service.dtoResponse.ImageResponse;
import com.dcode.product_service.entity.Image;
import com.dcode.product_service.entity.Product;

import java.util.UUID;

public class ImageUtils {
    public static Image createNewImageEntity(ImageRequest imageRequest, Product product){
        return Image.builder()
                .imageId(UUID.randomUUID().toString())
                .url(imageRequest.getUrl())
                .product(product)
                .build();
    }
    public static ImageResponse fromImageEntity(Image image){
        return ImageResponse.builder()
                .imageId(image.getImageId())
                .url(image.getUrl())
                .build();
    }

}
