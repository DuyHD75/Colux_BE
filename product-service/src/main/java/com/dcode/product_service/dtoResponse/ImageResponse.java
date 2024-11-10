package com.dcode.product_service.dtoResponse;

import com.dcode.product_service.entity.Product;
import com.dcode.product_service.enumeration.ImageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageResponse {
    private String imageId;
    private String url;
    private ProductResponse product;
}
