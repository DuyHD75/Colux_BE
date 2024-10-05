package com.dcode.product_service.dtoRequest;

import com.dcode.product_service.enumeration.ImageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ImageRequest {
    private String url;
    private String imageName;
    private String caption;
    private String altText;
    private ImageType imageType;
    private String productId;
}
