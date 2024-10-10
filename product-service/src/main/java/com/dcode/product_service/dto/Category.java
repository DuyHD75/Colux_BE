package com.dcode.product_service.dto;

import lombok.Data;

@Data
public class Category {
    private Long id;
    private String createdAt;
    private String updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private String categoryId;
    private String name;
    private String thumbnail;

}
