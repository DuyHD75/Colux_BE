package com.dcode.product_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBrandDTO {
    private String brandId;
    private String name;
    private String code;
    private String status;
}
