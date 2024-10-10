package com.dcode.product_service.dtoRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColorRequest {
    private String name;
    private String image;
    private String code;
    private String hex;
    private String LRV;
    private boolean interior;
    private boolean exterior;
    private String description;
    private long colorTypeId;
}
