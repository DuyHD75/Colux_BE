package com.dcode.product_service.entity;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponseBuilder {
    public static <T> PageResponse<T> buildPageResponse(Page<T> page){
        return PageResponse.<T>builder()
                .content(page.getContent())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .number(page.getNumber())
                .build();
    }
}
